package hz.github.echo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EchoServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("Server has started...");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected from " + clientSocket.getRemoteSocketAddress());
            try (InputStream inputStream = clientSocket.getInputStream()) {
                try (OutputStream outputStream = clientSocket.getOutputStream()) {
                    handle(inputStream, outputStream);
                }
            } catch (Exception e) {

            }
            System.out.println("Client disconnected.");
        }
    }

    private static void handle(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        writer.write("Hello! I'm server\n");
        writer.flush();
        while (true) {
            String s = reader.readLine();
            if (s.equals("bye")) {
                writer.write("bye\n");
                writer.flush();
                break;
            }
            writer.write("OK: " + s + "\n");
            writer.flush();
        }
    }
}
