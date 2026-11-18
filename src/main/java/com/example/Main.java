package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        try (ServerSocket ss = new ServerSocket(3000)) {
            System.out.println("Server avviato sulla porta 3000...");

            while (true) {
                Socket s = ss.accept();
                System.out.println("WELCOME");

                Thread t = new mioThread(s);
                t.start();
            }
        }
    }
}