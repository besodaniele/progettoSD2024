package it.unimib.sd2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import it.unimib.sd2024.DBEngine.DBParser;
import it.unimib.sd2024.DBEngine.PaneDB; 

/**
 * Classe principale in cui parte il database.
 */
public class Main {
    /**
     * Porta di ascolto.
     */
    public static final int PORT = 3030;

    // Objectmapper
    public static final Jsonb jsonb = JsonbBuilder.create();

    /**
     * Avvia il database e l'ascolto di nuove connessioni.
     */
    public static void startServer() throws IOException {

        var server = new ServerSocket(PORT);

        System.out.println("Database listening at localhost:" + PORT);
        try {
            while (true)
                new Handler(server.accept()).start();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            server.close();
        }
    }

    /**
     * Handler di una connessione del client.
     */
    private static class Handler extends Thread {
        private Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        public void run() {
            System.out.println("Connected to client " + client.getInetAddress() + ":" + client.getPort());
            var commando = "";
            try {
                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                while (!commando.equals("close")) {
                    commando = in.readLine();
                    
                    if(!commando.equals("close")){
                        var risposta = DBParser.getParser().parse(commando);
                        out.println(risposta);
                    }
                }
                

                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    // @TODO: implementare i metodi di setup
    private static void setupUtenti(Map utenti) {
        PaneDB.getDB().createTable("utenti", utenti);
    }

    private static void setupDomini(Map domini) {
        PaneDB.getDB().createTable("domini", domini);
    }

    private static void setupAcquisti(Map acquisti) {
        PaneDB.getDB().createTable("acquisti", acquisti);
    }

    public static Map fetchJsonFromFile(String pathToFile) throws IOException {
        File file = new File(pathToFile);
        String content = new String(Files.readAllBytes(file.toPath()));
        Map rootMap = jsonb.fromJson(content, new HashMap<String, Object>(){}.getClass().getGenericSuperclass());
        if (rootMap != null) {
            return rootMap;
        } else {
            throw new IOException("Il file JSON non contiene un oggetto JSON.");
        }
    }

    public static void main(String[] args) throws IOException {
        // caricamento file di configurazione
        Map acquisti = fetchJsonFromFile("./configurationFile/acquisti.json");
        Map domini = fetchJsonFromFile("./configurationFile/domini.json");
        Map utenti = fetchJsonFromFile("./configurationFile/utenti.json");

        // setup dei dati
        setupUtenti(utenti);
        setupDomini(domini);
        setupAcquisti(acquisti);

        // avvio del server
        startServer();

    }

}
