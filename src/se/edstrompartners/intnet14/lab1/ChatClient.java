package se.edstrompartners.intnet14.lab1;

import static se.edstrompartners.intnet14.lab1.ChatServer.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ChatClient {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private Socket sock;
    private InputStream in;
    private OutputStream out;
    private byte[] buf;
    private String name;

    public static void main(String[] args) throws UnknownHostException {
        ChatClient cc = new ChatClient(InetAddress.getByName("localhost"), 8080, "Actimia");
        cc.start();
    }

    public ChatClient(InetAddress host, int port, String name) {
        try {
            sock = new Socket(host, port);
            in = sock.getInputStream();
            out = sock.getOutputStream();
            buf = new byte[4096];

            this.name = name;
            out.write(name.getBytes(UTF8));
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        Thread t = new Thread(new ListenThread());
        t.setDaemon(true);
        t.start();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                String line = sc.nextLine();

                if (line.equals("/exit")) {
                    break;
                }

                send(toUtf8(name + ": " + line));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send(byte[] data) throws IOException {
        out.write(data);
        out.flush();
    }

    private class ListenThread implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    int len = in.read(buf);

                    if (len == -1) {
                        System.out.println("INFO: Connection to server lost.");
                        return;
                    }

                    byte[] data = new byte[len];
                    System.arraycopy(buf, 0, data, 0, len);
                    System.out.println(fromUtf8(data));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    sock.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
