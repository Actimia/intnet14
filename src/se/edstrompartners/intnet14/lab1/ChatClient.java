package se.edstrompartners.intnet14.lab1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandListener;
import se.edstrompartners.net.events.*;

/**
 * Chat client class. Call with arguments serveradress, port and username.
 * 
 * @author Viking & Fredrik
 * 
 */
public class ChatClient implements CommandListener {

    private static final Pattern WHISPER = Pattern.compile("/(?:w|pm) ([\\w]+) ([\\S ]+)");

    private Network net;
    private String name;

    private ChatGUI gui;

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
            new ChatClient(InetAddress.getByName(host), port, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ChatClient connect(String host, int port, String name, ChatGUI gui)
            throws IOException {
        ChatClient cc = new ChatClient(InetAddress.getByName(host), port, name, gui);
        return cc;
    }

    public ChatClient(InetAddress host, int port, String name) throws IOException {
        this(host, port, name, new ChatGUI() {
            @Override
            public void print(String type, String msg) {
                System.out.println(type + ":");
                System.out.println(msg);
            }
        });
    }

    public ChatClient(InetAddress host, int port, String name, ChatGUI gui) throws IOException {
        net = new Network(this, new Socket(host, port));
        this.name = name;
        Thread t = new Thread(net);
        t.start();

        net.send(new Handshake(name));
        this.gui = gui;
    }

    public boolean input(String line) {
        try {
            if (line.startsWith("/")) {
                // switch on first word of command
                switch (line.substring(0,
                        line.indexOf(' ') == -1 ? line.length() : line.indexOf(' '))) {
                case "/exit":
                    net.close();
                    return true;
                case "/listusers":
                    net.send(new ListUsers());
                    break;
                case "/help":
                    gui.print("help", "These are the available commands:");
                    gui.print("help", "  /exit - shuts down the program.");
                    gui.print("help", "  /help - displays this information.");
                    gui.print("help", "  /listusers - lists all users in this chat.");
                    gui.print("help", "  /w NAME MESSAGE - send private message.");
                    gui.print("help", "  /pm NAME MESSAGE - synonym for /w.");
                    break;
                case "/w":
                case "/pm":
                    // use a regex to find the target and the message with groups
                    Matcher match = WHISPER.matcher(line);
                    if (match.matches() && match.groupCount() == 2) {
                        String tar = match.group(1);
                        String msg = match.group(2);
                        net.send(new PrivateMessage(name, tar, msg));
                        gui.print("whisper", String.format("To %s: %s", tar, msg));
                    } else {
                        gui.print("help", "Private message syntax: /w NAME MESSAGE");
                    }
                    break;
                default:
                    gui.print("help", "Unknown command, type /help for a list.");
                    break;
                }
            } else {
                net.send(new Message("message", name + ": " + line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCommand(Network src, Command com) throws IOException {
        switch (com.getType()) {
        case MESSAGE:
            Message msg = (Message) com;
            gui.print(msg.type, msg.message);
            break;
        case PRIVATEMESSAGE:
            PrivateMessage pm = (PrivateMessage) com;
            gui.print("whisper", String.format("From %s: %s", pm.src, pm.msg));
            // System.out.printf("  Private message from %s:%n    %s%n", pm.src, pm.msg);
            break;
        case LISTUSERSRESPONSE:
            ListUsersResponse lur = (ListUsersResponse) com;
            gui.print("server", "User list:");
            for (String user : lur.users) {
                gui.print("server", "  " + user);
            }
            break;
        case NETWORKSHUTDOWN:
            gui.print("error", "Network connection lost.");
            break;
        default:
            break;
        }
    }

    public void close() {
        try {
            net.close();
        } catch (IOException e) {
        }
    }

}
