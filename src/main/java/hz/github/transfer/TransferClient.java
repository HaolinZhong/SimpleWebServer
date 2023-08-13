package hz.github.transfer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        BufferedReader charReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        BufferedInputStream reader = new BufferedInputStream(inputStream);

//        System.out.println("[server] " + reader.read());


        System.out.println("Sending request for " + fileName);
        writer.write(fileName);
        writer.newLine();
        writer.flush();


        String respHeader = charReader.readLine();
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

        System.out.println("file size: " + fileSize);

        int bufferSize = 256;
        byte[] byteBuffer = new byte[bufferSize];

        try (OutputStream fileWriter = Files.newOutputStream(Paths.get("files/client/" + fileName))) {
            while (fileSize > 0) {
                int readSize = Math.min(bufferSize, fileSize);
                readSize = reader.read(byteBuffer, 0, readSize);
                fileWriter.write(byteBuffer, 0, readSize);
                fileSize -= readSize;
            }
        }

        System.out.println("Out of transfer");
        String respTail = charReader.readLine();
        System.out.println(respTail);
        System.out.println("Transfer completed!");
    }


}
