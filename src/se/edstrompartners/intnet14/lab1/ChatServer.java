package se.edstrompartners.intnet14.lab1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int port = 8080;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private ServerSocket ssocket;

    private ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        new ChatServer().start();
    }

    public ChatServer() {
        try {
            ssocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        try {
            System.out.println("Accepting connections.");
            while (true) {
                // recieve new connections
                ClientHandler st = new ClientHandler(ssocket.accept());
                Thread t = new Thread(st);
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {

        }
    }

    private boolean register(String name, ClientHandler handler) throws IOException {
        if (clients.containsKey(name)) {
            // already contains a client with this name
            return false;
        } else {
            clients.put(name, handler);
            System.out.println(name + " connected.");
            send(name, toUtf8("INFO: This is a server MOTD."));
            return true;
        }
    }

    public void unregister(String name) {
        clients.remove(name);
        System.out.println(name + " disconnected.");
    }

    private boolean send(String name, byte[] message) throws IOException {
        ClientHandler st = clients.get(name);
        if (st == null) {
            return false;
        }
        st.send(message);
        System.out.println("Sent message to " + name);
        return true;
    }

    private void broadcast(byte[] message) throws IOException {
        for (ClientHandler st : clients.values()) {
            st.send(message);
        }
    }

    private class ClientHandler implements Runnable {

        private Socket sock;
        private InputStream in;
        private OutputStream out;
        private byte[] buf;

        private String name;

        private ClientHandler(Socket s) throws IOException {
            // setup
            sock = s;
            in = s.getInputStream();
            out = s.getOutputStream();
            buf = new byte[4096];
        }

        public void send(byte[] message) throws IOException {
            out.write(message);
            out.flush();
        }

        @Override
        public void run() {
            try {
                // handshake, recieve name from client
                {
                    int len = in.read(buf);
                    name = new String(buf, 0, len, UTF8);
                    if (!register(name, this)) {
                        // registration failed, send error and shutdown
                        send(toUtf8("A user with that name is already connected."));
                        return;
                    }
                }
                while (true) {
                    int len = in.read(buf);
                    if (len == -1) {
                        // clean exit, do the same here
                        return;
                    }

                    byte[] data = new byte[len];
                    System.arraycopy(buf, 0, data, 0, len);
                    broadcast(data);
                    System.out.println(fromUtf8(data));
                }
            } catch (IOException e) {

            } finally {
                // always do cleanup
                try {
                    in.close();
                    out.close();
                    sock.close();
                    unregister(name);
                } catch (IOException e) {
                }
            }
        }
    }

    public static byte[] toUtf8(String s) {
        return s.getBytes(UTF8);
    }

    public static String fromUtf8(byte[] bytes) {
        return new String(bytes, UTF8);
    }

}
