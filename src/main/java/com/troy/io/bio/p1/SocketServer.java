package com.troy.io.bio.p1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static void main(String[] args) {
        System.out.println("test");
        try {
            ServerSocket serverSocket = new ServerSocket(9000);
            while (true){
                Socket socket = serverSocket.accept();
                if (socket != null){
                    System.out.println("socket已连接");
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String content = bufferedReader.readLine();
                    System.out.println("test");
                    System.out.println("receive msg:" + content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
