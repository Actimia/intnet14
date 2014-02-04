package se.edstrompartners.intnet14.lab2;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private static HashMap<String, Client> clients = new HashMap<>();
    private static final Random rnd = new Random();

    private static final ExecutorService tp = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        @SuppressWarnings("resource")
        ServerSocket ss = new ServerSocket(8080);
        while (true) {
            final Socket s = ss.accept(); // listens to new connections
            tp.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        // Read and parse the whole request
                        byte[] buf = new byte[2048];
                        InputStream in = s.getInputStream();
                        int num = in.read(buf);
                        String request = new String(buf, 0, num, Charset.forName("UTF-8"));
                        Request req = Request.parse(request);

                        // ignore favicon gets
                        if (req.file.equals("/favicon.ico")) {
                            PrintStream response = new PrintStream(s.getOutputStream());
                            response.println("HTTP/1.1 200 OK");
                            s.shutdownOutput();
                            s.close();
                            return;
                        }

                        if (req.method.equals("GET")) {
                            // static get
                            serveStatic(s);
                        } else {
                            // POST
                            if (req.cookie == null || clients.get(req.cookie) == null) {
                                // new client
                                handleNewClient(req, s);
                            } else {
                                // old client
                                handleClient(req, s);
                            }
                        }
                        // cleanup
                        s.shutdownInput();
                        s.shutdownOutput();
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private static void serveStatic(Socket s) throws IOException {
        // simply write top and bottom with a welcome msg
        PrintStream response = new PrintStream(s.getOutputStream());
        response.println("HTTP/1.1 200 OK");
        response.println("Content-Type: text/html");
        response.println();
        Files.lines(Paths.get("top.html")).forEach((line) -> response.println(line));
        response.println("Welcome to the guessing game! Please enter a number between 1 and 100.");
        Files.lines(Paths.get("bottom.html")).forEach((line) -> response.println(line));
    }

    private static void handleClient(Request req, Socket s) throws IOException {
        // fetch client state, serve html
        Client client = clients.get(req.cookie);
        PrintStream response = new PrintStream(s.getOutputStream());
        response.println("HTTP/1.1 200 OK");
        response.println("Content-Type: text/html");
        response.println();
        handleCommon(req, client, response);
    }

    private static void handleCommon(Request req, Client client, PrintStream response)
            throws IOException {
        Files.lines(Paths.get("top.html")).forEach((line) -> response.println(line));

        response.println("You guessed " + req.guess + ".");
        client.guesses++;
        System.out.println(req.cookie + " guessed " + req.guess + " (" + client.guesses + ")");

        int guess = Integer.parseInt(req.guess);
        if (guess > client.answer) {
            response.println("That was too high, guess lower!");
        } else if (guess < client.answer) {
            response.println("That was too low, guess higher!");
        } else {
            // correct answer, reset game
            response.println("That was correct! It took you " + client.guesses + " guesses!");
            response.println("<br>A new number has been generated for you if you want to keep guessing.");
            client.answer = rnd.nextInt(100);
            client.guesses = 0;
        }

        Files.lines(Paths.get("bottom.html")).forEach((line) -> response.println(line));
    }

    private static void handleNewClient(Request req, Socket s) throws IOException {
        // create and save cookie
        String cookie = "" + (rnd.nextInt(Integer.MAX_VALUE));
        Client client = new Client();
        client.cookie = cookie;
        client.answer = rnd.nextInt(100);
        clients.put(cookie, client);

        // transmit cookie in response
        PrintStream response = new PrintStream(s.getOutputStream());
        response.println("HTTP/1.1 200 OK");
        response.println("Content-Type: text/html");
        response.println("Set-Cookie: SESSIONID=" + cookie);
        response.println();
        handleCommon(req, client, response);
    }
}