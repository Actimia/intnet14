package se.edstrompartners.intnet14.lab1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandListener;
import se.edstrompartners.net.command.ListUsers;
import se.edstrompartners.net.events.Handshake;
import se.edstrompartners.net.events.Message;

/**
 * Chat client class. Call with arguments serveradress, port and username.
 * 
 * @author Viking & Fredrik
 * 
 */
public class ChatClient implements CommandListener {

    private Network net;
    private String name;

    public static void main(String[] args) {
        try {
            String host;
            int port;
            String name;
            if (args.length == 0) {
                host = "localhost";
                port = 8080;
                Random rnd = new Random();
                name = "Guest_" + rnd.nextInt(100);

            } else {
                host = args[0];
                port = Integer.parseInt(args[1]);
                name = args[2];
            }
            new ChatClient(InetAddress.getByName(host), port, name).start();
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
                if (line.startsWith("/")) {
                    switch (line) {
                    case "/exit":
                        net.shutdown();
                        return;
                    case "/listusers":
                        net.send(new ListUsers());
                        break;
                    case "/help":
                        System.out.println("These are the available commands:");
                        System.out.println("\t/exit - shuts down the program.");
                        System.out.println("\t/help - displays this information.");
                        System.out.println("\t/listusers - lists all users in this chat.");
                        break;
                    default:
                        System.out.println("Unknown command, type /help for a list.");
                        break;
                    }
                } else {
                    net.send(new Message(name, line));
                }
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
