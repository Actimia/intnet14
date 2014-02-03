package se.edstrompartners.intnet14.lab1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandListener;
import se.edstrompartners.net.events.NetworkShutdown;

public class Network implements Runnable {
    private final CommandListener cl;
    private final InputStream in;
    private final OutputStream out;
    private final Socket sock;
    private String token;
    private final byte[] buf = new byte[1024];

    private boolean closed = false;

    public Network(CommandListener cl, Socket sock) throws IOException {
        this.cl = cl;
        this.sock = sock;
        in = sock.getInputStream();
        out = sock.getOutputStream();
    }

    public void send(Command com) throws IOException {
        byte[] data = Command.encode(com);
        out.write(data);
    }

    public void setToken(String s) {
        token = s;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int len = in.read(buf);
                if (len == -1) {
                    // System.out.println("INFO: Connection lost.");
                    return;
                }

                byte[] data = new byte[len];
                System.arraycopy(buf, 0, data, 0, len);
                cl.onCommand(this, Command.decode(data));
            }
        } catch (IOException e) {
            if (!closed) {
                e.printStackTrace();
            }
        } finally {
            try {
                shutdown();
            } catch (IOException e) {
                if (!closed) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void shutdown() throws IOException {
        in.close();
        out.close();
        sock.close();
        if (!closed) {
            cl.onCommand(this, new NetworkShutdown(token));
        }
    }

    public void close() throws IOException {
        in.close();
        out.close();
        sock.close();
        closed = true;
    }

}