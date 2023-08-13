package hz.github.transfer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TransferServer {

    public static void main(String[] args) throws IOException {
        // 指定监听端口创建server socket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("Server has started...");

        // 循环接受连接
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
            System.out.println(s);
            File file = new File("files/server/" + s);

            if (!file.exists() || !file.isFile()) {
                System.out.println("Error: File does not exist!\n");
                writer.write("Error: File does not exist!\n");
                writer.flush();
                break;
            }



            long fileSize = file.length();

            writer.write("size:" + fileSize);
            writer.newLine();
            writer.flush();

            FileReader fileReader = new FileReader("files/server/" + s);

            int bufferSize = 256;
            char[] charBuffer = new char[bufferSize];

            while (fileSize > 0) {
                int readSize = (int) Math.min(bufferSize, fileSize);
                fileReader.read(charBuffer, 0, readSize);
                System.out.println(charBuffer);
                writer.write(charBuffer, 0, readSize);
                fileSize -= readSize;
            }

            writer.write("OK: " + s + "has been sent\n");
            writer.flush();
        }

    }
}
