package se.edstrompartners.intnet14.lab1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandListener;
import se.edstrompartners.net.events.Handshake;
import se.edstrompartners.net.events.Message;
import se.edstrompartners.net.events.NetworkShutdown;

public class ChatServer implements CommandListener {
    private static final int port = 8080;

    private ServerSocket ssocket;
    private Network net;

    private ConcurrentHashMap<String, Network> clients = new ConcurrentHashMap<>();

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
                net = new Network(this, ssocket.accept());
                Thread t = new Thread(net);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean register(Network handler, Handshake hs) throws IOException {
        String name = hs.name;
        if (clients.containsKey(name)) {
            // already contains a client with this name
            handler.send(new Message("SERVER", "A user with that name already exists."));
            handler.shutdown();
            return false;
        } else {
            broadcast(new Message("SERVER", name + " connected."));
            clients.put(name, handler);
            System.out.println(name + " connected.");
            handler.setToken(name);
            handler.send(new Message("SERVER", "This is the server motd"));
            return true;
        }
    }

    public void unregister(String name) throws IOException {
        if (name == null) {
            return;
        }
        Network handler = clients.remove(name);
        broadcast(new Message("SERVER", name + " disconnected."));
        System.out.println(name + " disconnected.");

    }

    private boolean send(String name, Command com) throws IOException {
        Network st = clients.get(name);
        if (st == null) {
            return false;
        }
        st.send(com);
        // System.out.println("Sent message to " + name);
        return true;
    }

    private void broadcast(Command com) throws IOException {
        // System.out.println("Broadcasting to all users.");
        for (Network client : clients.values()) {
            client.send(com);
        }
    }

    @Override
    public void onCommand(Network src, Command com) throws IOException {
        switch (com.getType()) {
        case HANDSHAKE:
            register(src, (Handshake) com);
            break;
        case MESSAGE:
            broadcast(com);
            break;
        case NETWORKSHUTDOWN:
            unregister(((NetworkShutdown) com).token);
            break;
        case LISTUSERS:
            src.send(new Message("USERS", getUserList()));
            break;
        default:
            throw new AssertionError("Unknown command recieved by server: " + com.getType().name());
        }
    }

    private String getUserList() {
        StringBuilder sb = new StringBuilder();
        for (String name : clients.keySet()) {
            sb.append("\n\t");
            sb.append(name);
        }
        return sb.toString();
    }

}
