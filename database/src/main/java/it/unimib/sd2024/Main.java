package it.unimib.sd2024;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
    public static final ObjectMapper mapper = new ObjectMapper();

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
            try {
                var out = new PrintWriter(client.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                /* var commando = in.readLine();
                var risposta = DBParser.getParser().parse(commando);

                out.println(risposta);
                */
                System.out.println(DBParser.getParser().parse("get utenti.*.* where nome=Luca"));
                System.out.println(PaneDB.getDB().getTable("utenti").toString());

                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    // @TODO: implementare i metodi di setup
    private static void setupUtenti(JsonNode utenti) {
        PaneDB.getDB().createTable("utenti", utenti);
    }

    private static void setupDomini(JsonNode domini) {
        PaneDB.getDB().createTable("domini", domini);
    }

    private static void setupAcquisti(JsonNode acquisti) {
        PaneDB.getDB().createTable("acquisti", acquisti);
    }

    public static ObjectNode fetchJsonFromFile(String pathToFile) throws IOException {
        File file = new File(pathToFile); //si prende il file dal path
        JsonNode rootNode = mapper.readTree(file); //leggo il file JSON e lo trasformo in un JsonNode

        if (rootNode.isObject()) {
            return (ObjectNode) rootNode;
        } else {
            throw new IOException("Il file JSON non contiene un oggetto JSON.");
        }
    }

    public static void main(String[] args) throws IOException {
        // caricamento file di configurazione
        JsonNode acquisti = fetchJsonFromFile("./configurationFile/acquisti.json");
        JsonNode domini = fetchJsonFromFile("./configurationFile/domini.json");
        JsonNode utenti = fetchJsonFromFile("./configurationFile/utenti.json");

        // setup dei dati
        setupUtenti(utenti);
        setupDomini(domini);
        setupAcquisti(acquisti);

        // avvio del server
        startServer();

    }

}
