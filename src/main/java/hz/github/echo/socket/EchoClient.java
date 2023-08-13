package hz.github.echo.socket;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EchoClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 6666);
        try (InputStream inputStream = socket.getInputStream()) {
            try (OutputStream outputStream = socket.getOutputStream()) {
                handle(inputStream, outputStream);
            }
        }

        socket.close();
        System.out.println("Disconnected");
    }

    private static void handle(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(System.in);

        System.out.println("[server] " + reader.readLine());

        while (true) {
            System.out.print(">>> ");
            String s = scanner.nextLine();
            writer.write(s);
            writer.newLine();
            writer.flush();
            String resp = reader.readLine();
            System.out.println("<<< " + resp);
            if (resp.equals("bye")) {
                break;
            }
        }
    }
}
