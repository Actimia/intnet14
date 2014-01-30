package se.edstrompartners.intnet14.lab1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandListener;
import se.edstrompartners.net.events.Handshake;
import se.edstrompartners.net.events.Message;

public class ChatClient implements CommandListener {
    private Network net;
    private String name;

    public static void main(String[] args) {
        try {
            ChatClient cc = new ChatClient(InetAddress.getByName("localhost"), 8080, "Actimia");
            cc.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatClient(InetAddress host, int port, String name) throws IOException {
        net = new Network(this, new Socket(host, port));
        this.name = name;
    }

    private void start() {
        try (Scanner sc = new Scanner(System.in)) {
            Thread t = new Thread(net);
            t.start();

            net.send(new Handshake(name));
            while (true) {
                String line = sc.nextLine();
                if (line.equals("/exit")) {
                    net.shutdown();
                    return;
                }
                net.send(new Message(name, line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCommand(Network src, Command com) throws IOException {
        switch (com.getType()) {
        case MESSAGE:
            Message msg = (Message) com;
            System.out.println(msg.source + ": " + msg.message);
            break;
        case NETWORKSHUTDOWN:
            System.out.println("INFO: Network connection lost.");
            System.exit(0);
            break;
        default:
            break;
        }
    }

}
