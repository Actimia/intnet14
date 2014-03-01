package se.edstrompartners.intnet14.lab6;

import java.io.*;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {
    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\actim_000\\.keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "viking93");

        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        String[] ciphers = ssf.getSupportedCipherSuites();

        System.out.println("Supports " + ciphers.length + " cipher suites.");
        SSLServerSocket ss = null;

        try {
            ss = (SSLServerSocket) ssf.createServerSocket(1234);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ss.setEnabledCipherSuites(ciphers);

        while (true) {
            try {
                SSLSocket s = (SSLSocket) ss.accept();
                OutputStream out = s.getOutputStream();
                out.write("HTTP/1.1 200 OK\nContent-Type: text/plain\n\nHello, World!"
                        .getBytes(Charset.forName("UTF-8")));
                out.flush();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void loadCertificate() {
        try {
            InputStream certfile = new FileInputStream("server.cer");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(certfile);
            certfile.close();
        } catch (CertificateException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchProviderException e) {
            System.out.println(e.getMessage());
        }

        InputStream is = null;
        try {
            is = new FileInputStream(new File("C:\\Users\\actim_000\\.keystore"));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            ks.load(is, "viking93".toCharArray());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        } catch (CertificateException e) {
            System.out.println(e.getMessage());
        }
    }
}
