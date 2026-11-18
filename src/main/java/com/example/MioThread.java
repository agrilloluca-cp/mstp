package com.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MioThread extends Thread {

    private Socket s;

    public void mioThread(Socket s)  {
            this.s = s;

    }

 @Override
    public void run() {
        try (Socket socket = this.s;
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                 new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String richiesta = in.readLine();
            if (richiesta == null) {
                return;
            }

            System.out.println("Ricevuto: " + richiesta);

            String richiestaPulita = richiesta.trim();
            if (richiestaPulita.isEmpty()) {
                inviaErrore(out, "ERR SYNTAX");
                return;
            }

            String[] parti = richiestaPulita.split("\\s+");

            if (!parti[0].equals("LEGGI")) {
                inviaErrore(out, "ERR UNKNOWNCMD");
                return;
            }

            if (parti.length != 2 || !isNomeValido(parti[1])) {
                inviaErrore(out, "ERR SYNTAX");
                return;
            }

            String nomeFile = parti[1];
            File cartella = new File("documenti");
            File file = new File(cartella, nomeFile);

            System.out.println("Cerco in: " + file.getAbsolutePath());

            if (!file.isFile()) {
                inviaErrore(out, "ERR NOTFOUND");
                return;
            }

            boolean statoInviato = false;
            try (BufferedReader lettoreFile = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                out.println("OK FILE");
                out.println();

                String riga;
                while ((riga = lettoreFile.readLine()) != null) {
                    out.println(riga);
                }
            } catch (IOException e) {
                if (!statoInviato) {
                    inviaErrore(out, "ERR READ");
                } else {
                    System.err.println("Errore durante l'invio del file: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Errore di comunicazione con il cliente: " + e.getMessage());
        }
    }

    private boolean isNomeValido(String nome) {
        if (nome.contains("/") || nome.contains("\\") || nome.contains("..")) {
            return false;
        }
        if (!nome.endsWith(".txt") || nome.equals(".txt")) {
            return false;
        }
        return true;
    }

    private void inviaErrore(PrintWriter out, String errore) {
        out.println(errore);
        out.println();
    }
}

