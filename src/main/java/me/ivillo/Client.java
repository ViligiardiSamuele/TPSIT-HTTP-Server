package me.ivillo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client extends Thread {

    private Boolean enable;
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;
    private List<String> buffer;

    public Client(Socket socket) {
        this.enable = true;
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.buffer = new ArrayList<String>();
    }

    @Override
    public void run() {
        String input;
        StringBuilder sb = new StringBuilder("-- From [" + socket.getPort() + "] -----\n");
        try {
            while (enable && in.ready()) {
                input = in.readLine();
                buffer.add(input);
                if (input.equals("")) {
                    execute();
                    System.out.println(sb.append("-------------\n\n").toString());
                    sb.delete(0, sb.length());
                    sb.append("\n\n--" + socket.getPort() + "------\n");
                } else sb.append(input + "\n");

            }
            out.close();
            in.close();
            socket.close();
            System.out.println(socket.getPort() + ": Ha interrotto la connessione");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void execute() throws IOException {
        String[] string0 = buffer.get(0).split("\\s+", 0);
        File file = new File("htdocs" + string0[1]);
        if (file.exists()) {
            if (file.isDirectory()) {
                System.out.println(socket.getPort() + ": Invio di: " + file.getPath() + "/index.html" + " (" + getContentType(file) + ")");
                sendBinaryFile(out, new File(file.getPath() + "/index.html"));
            } else {
                System.out.println(socket.getPort() + ": Invio di: " + file.getPath() + " (" + getContentType(file) + ")");
                sendBinaryFile(out, file);
            }
        } else {
            System.out.println("File non trovato: " + file.getPath());
            String msg = "<h1>>:(</h1>";
            try {
                out.writeBytes("HTTP/1.1 404 Not Found\n");
                out.writeBytes("Date: " + LocalDateTime.now().toString() + "\n");
                out.writeBytes("Server: meucci-server\n");
                out.writeBytes("Content-Type: text/html\n");
                out.writeBytes("Content-Length: " + msg.length() + "\n");
                out.writeBytes("\n");
                out.writeBytes(msg + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFile(File file) {
        try {
            out.writeBytes("HTTP/1.1 200 OK\n");
            out.writeBytes("Date: " + LocalDateTime.now().toString() + "\n");
            out.writeBytes("Server: meucci-server\n");
            out.writeBytes("Content-Type: " + getContentType(file) + "\n");
            out.writeBytes("Content-Length: " + file.length() + "\n");
            out.writeBytes("\n");
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                out.writeBytes(fileReader.nextLine() + "\n");
            }
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getContentType(File f) {
        String[] s = f.getName().split("\\.");
        String ext = s[s.length - 1];
        switch (ext) {
            case "html":
            case "htm":
                return "text/html";
            case "png":
                return "image/png";
            case "css":
                return "text/css";
            default:
                return "";
        }

    }

    private void sendBinaryFile(DataOutputStream output, File file) throws IOException {
        output.writeBytes("HTTP/1.1 200 OK\n");
        output.writeBytes("Content-Length: " + file.length() + "\n");
        output.writeBytes("Content-Type: " + getContentType(file) + "\n");
        output.writeBytes("\n");
        InputStream input = new FileInputStream(file);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            output.write(buf, 0, n);
        }
        input.close();
    }
}
