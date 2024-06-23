package it.unimib.sd2024;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {
    private static int port = 3030;
    private static String host = "localhost";
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Connection() throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(String message) throws IOException {
        out.println(message);
    }

    public String receive() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        out.println("close");
        in.close();
        out.close();
        socket.close();
    }

}
