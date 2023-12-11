package me.ivillo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Client extends Thread{

    private Boolean enable;
    private Socket socket;
    BufferedReader in;
    DataOutputStream out;
    private List<String> buffer;

    public Client(Socket socket){
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
        try {
            while(enable){
                input = in.readLine();
                buffer.add(input);
                if(input.equals(""))
                    execute();
            }
            out.close();
            in.close();
            socket.close();
            System.out.println(socket.getPort() + " ha interrotto la connessione");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void execute(){
        String[] string0 = buffer.get(0).split("\\s+",0);
        File file = new File("htdocs" + string0[1]);
        if(file.exists() && !file.isDirectory()) { 
            System.out.println("Invio di: " + file.getPath());
            try {
                out.writeBytes("HTTP/1.1 200 OK\n");
                out.writeBytes("Date: " + new Date().toString() + "\n");
                out.writeBytes("Server: meucci-server\n");
                out.writeBytes("Content-Type: text/plain; charset=UTF-8\n");
                out.writeBytes("Content-Length: " + file.length() + "\n");
                out.writeBytes("\n");
                Scanner fileReader = new Scanner(file);
                while(fileReader.hasNextLine()){
                    out.writeBytes(fileReader.nextLine() + "\n");
                }
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File non trovato: " + file.getPath());
            String msg = "The resource was not found";
            try {
                out.writeBytes("HTTP/1.1 404 Not Found\n");
                out.writeBytes("Date: " + new Date().toString() + "\n");
                out.writeBytes("Server: meucci-server\n");
                out.writeBytes("Content-Type: text/plain; charset=UTF-8\n");
                out.writeBytes("Content-Length: " + msg.length() + "\n");
                out.writeBytes("\n");
                out.writeBytes(msg + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.enable = false;
    }
}
