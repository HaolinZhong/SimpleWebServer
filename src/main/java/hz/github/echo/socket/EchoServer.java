package hz.github.echo.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EchoServer {

    public static void main(String[] args) throws IOException {
        // 指定监听端口创建server socket
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("Server has started...");

        // 循环接受连接
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected from " + clientSocket.getRemoteSocketAddress());

            /*
            *
            * 因为TCP是一种基于流的协议，因此，Java标准库使用InputStream和OutputStream来封装Socket的数据流，
            * 这样我们使用Socket的流，和普通IO流类似：
            *
            * */

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

        /*
        *
        * 如果不调用flush()，我们很可能会发现，客户端和服务器都收不到数据，
        * 这并不是Java标准库的设计问题，而是我们以流的形式写入数据的时候，
        * 并不是一写入就立刻发送到网络，而是先写入内存缓冲区，直到缓冲区满了以后，
        * 才会一次性真正发送到网络，这样设计的目的是为了提高传输效率。
        * 如果缓冲区的数据很少，而我们又想强制把这些数据发送到网络，
        * 就必须调用flush()强制把缓冲区数据发送出去。
        *
        * */

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
