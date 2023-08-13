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
            clientSocket.close();
            System.out.println("Client disconnected.");
        }
    }

    private static void handle(InputStream inputStream, OutputStream outputStream) throws IOException {
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        BufferedOutputStream writer = new BufferedOutputStream(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        writer.write("Hello! I'm server\n".getBytes());
//        writer.flush();

        while (true) {
            String s = reader.readLine();
            if (s.equals("bye")) {
                writer.write("bye\n".getBytes());
                writer.flush();
                break;
            }
            System.out.println(s);
            File file = new File("files/server/" + s);

            if (!file.exists() || !file.isFile()) {
                System.out.println("Error: File does not exist!\n");
                writer.write("Error: File does not exist!\n".getBytes());
                writer.flush();
                break;
            }



            long fileSize = file.length();

            writer.write(("size:" + fileSize).getBytes());
            writer.write("\n".getBytes());
            writer.flush();

//            FileReader fileReader = new FileReader("files/server/" + s);
            InputStream fileInput = new FileInputStream("files/server/" + s);

            int bufferSize = 256;
            byte[] byteBuffer = new byte[bufferSize];

            while (fileSize > 0) {
                System.out.println(fileSize);
                int readSize = (int) Math.min(bufferSize, fileSize);
                readSize = fileInput.read(byteBuffer, 0, readSize);
                writer.write(byteBuffer, 0, readSize);
                writer.flush();
                fileSize -= readSize;
            }

            writer.write(("OK: " + s + " has been sent\n").getBytes());
            writer.write("\n".getBytes());
            writer.flush();
        }

    }
}
