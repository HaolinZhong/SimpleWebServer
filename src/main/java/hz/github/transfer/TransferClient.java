package hz.github.transfer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TransferClient {

    private static String fileName = null;

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.err.println("Please specify filename");
            System.exit(1);
        }

        fileName = args[0];


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

        System.out.println("[server] " + reader.readLine());


        System.out.println("Sending request for " + fileName);
        writer.write(fileName);
        writer.newLine();
        writer.flush();
        String respHeader = reader.readLine();
        if (respHeader.startsWith("Error:")) {
            System.out.println(respHeader);
        }

        int fileSize = 0;

        for (String kvpair : respHeader.split(";")) {
            String[] keyValue = kvpair.split(":");
            String key = keyValue[0];
            String value = keyValue[1];
            if (key.equals("size")) {
                fileSize = Integer.parseInt(value);
            }
        }

        int bufferSize = 256;
        char[] charBuffer = new char[bufferSize];

        try (Writer fileWriter = new FileWriter("files/client/" + fileName)) {
            while (fileSize > 0) {
                int readSize = Math.min(bufferSize, fileSize);
                reader.read(charBuffer, 0, readSize);
                fileWriter.write(charBuffer, 0, readSize);
                fileSize -= readSize;
            }
        }

        System.out.println("Transfer completed!");
    }


}
