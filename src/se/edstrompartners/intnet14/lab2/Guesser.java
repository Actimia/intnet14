package se.edstrompartners.intnet14.lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class Guesser {

    public static void main(String[] args) throws IOException, InterruptedException {
        int low = 0;
        int high = 100;
        int mid = (low + high) / 2;
        Response first = guess(mid);
        // System.out.println(first.html);
    }

    private static Response guess(int guess) throws IOException {
        URL url = new URL("http://127.0.0.1:8080");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        // conn.setRequestProperty("Cookie", "SESSIONID=" + cookie);
        OutputStream out = conn.getOutputStream();
        out.write(("guess=" + guess).getBytes(Charset.forName("UTF-8")));
        out.flush();
        out.close();

        // String cookie = conn.getHeaderField("Set-Cookie");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        // String response = in.lines().collect(Collectors.joining("\n"));
        String str;
        while ((str = in.readLine()) != null) {
            System.out.println(str);
        }
        return null;
    }
}