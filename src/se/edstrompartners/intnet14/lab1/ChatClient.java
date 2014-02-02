package se.edstrompartners.intnet14.lab1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandListener;
import se.edstrompartners.net.command.ListUsers;
import se.edstrompartners.net.events.Handshake;
import se.edstrompartners.net.events.Message;
import se.edstrompartners.net.events.PrivateMessage;
import se.edstrompartners.net.gui.MainWindow;

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
	private MainWindow window;
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
	public Network getNetwork(){
		return net;
	}
	public ChatClient(InetAddress host, int port, String name) throws IOException {
		net = new Network(this, new Socket(host, port));
		window = new MainWindow(this,host+name);
		this.name = name;
	}

	private void start() {
		try (Scanner sc = new Scanner(System.in)) {
			Thread t = new Thread(net);
			t.start();

			net.send(new Handshake(name));
			while (true) {
				// System.out.print("  ");
				String line = sc.nextLine();
				handleInput(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void handleInput(String line)  {
		try {
			if (line.startsWith("/")) {
				// switch on first word of command
				switch (line.substring(0,
						line.indexOf(' ') == -1 ? line.length() : line.indexOf(' '))) {
						case "/exit":
							net.shutdown();
							return;
						case "/listusers":
							net.send(new ListUsers());
							break;
						case "/help":
							System.out.println("  These are the available commands:");
							System.out.println("    /exit - shuts down the program.");
							System.out.println("    /help - displays this information.");
							System.out.println("    /listusers - lists all users in this chat.");
							System.out.println("    /w NAME MESSAGE - send private message.");
							System.out.println("    /pm NAME MESSAGE - synonym for /w.");
							break;
						case "/w":
						case "/pm":
							// use a regex to find the target and the message with groups
							Matcher match = WHISPER.matcher(line);
							if (match.matches() && match.groupCount() == 2) {
								net.send(new PrivateMessage(name, match.group(1), match.group(2)));
							} else {
								System.out.println("  Private message syntax:\n    /w NAME MESSAGE");
							}
							break;
						default:
							System.out.println("  Unknown command, type /help for a list.");
							break;
				}
			} else {

				net.send(new Message(name, line));

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCommand(Network src, Command com) throws IOException {
		switch (com.getType()) {
		case MESSAGE:
			Message msg = (Message) com;
			if (msg.source.isEmpty()) {
				System.out.println("  " + msg.message);
				window.pushToWindow(msg.message);
			} else {
				System.out.println(msg.source + ": " + msg.message);
				window.pushToWindow(msg.source + ": " + msg.message);
			}
			break;
		case PRIVATEMESSAGE:
			PrivateMessage pm = (PrivateMessage) com;
			System.out.printf("  Private message from %s:%n    %s%n", pm.src, pm.msg);
			if (!pm.tar.equals(name)) {
				System.out.println("  Contact an admin. This pm was meant for " + pm.tar + ".");
			}
			break;
		case NETWORKSHUTDOWN:
			System.out.println("  Network connection lost.");
			System.exit(0);
			break;
		default:
			break;
		}
	}

}
