package me.ivillo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class App 
{
    public static void main( String[] args )
    {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server avviato!");
            for(;;){
                Socket socket = serverSocket.accept();
                Client client = new Client(socket);
                client.start();
                System.out.println(socket.getPort() + " si e' connesso");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
