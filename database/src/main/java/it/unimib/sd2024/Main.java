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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
                System.out.println("ecco la query:");
                System.out.println(PaneDB.getDB().get("", "m.rossi@gmail.com", ""));

                System.out.println("finished");

                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * Metodo principale di avvio del database.
     *
     * @param args argomenti passati a riga di comando.
     *
     * @throws IOException
     */
    private static ArrayNode readJson(String path) {
        try {
            return (ArrayNode) mapper.readTree(Main.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // @TODO: implementare i metodi di setup
    private static void setupUtenti(ArrayNode utenti) {
    }

    private static void setupDomini(ArrayNode domini) {
    }

    private static void setupAcquisti(ArrayNode acquisti) {
    }

    public static ObjectNode fetchJsonFromFile(String pathToFile) throws IOException {
        File file = new File(pathToFile);
        JsonNode rootNode = mapper.readTree(file);

        if (rootNode.isObject()) {
            return (ObjectNode) rootNode;
        } else {
            throw new IOException("Il file JSON non contiene un oggetto JSON.");
        }
    }

    public static void main(String[] args) throws IOException {
        // caricamento file di configurazione
        // ArrayNode acquisti = fetchJsonFromFile("./configurationFile/acquisti.json");
        // ArrayNode domini = fetchJsonFromFile("./configurationFile/domini.json");
        JsonNode utenti = fetchJsonFromFile("./configurationFile/utenti.json");

        PaneDB.getDB().setupTest(utenti);
        startServer();

        // setupUtenti(utenti);
        // setupDomini(domini);
        // setupAcquisti(acquisti);

    }
    /*
     * public static void setupUtenti(ArrayNode utenti) {
     * for (var utente : utenti) {
     * PaneDB.getDB().set("utente:"+utente.get("email").asText(), utente.asText());
     * }
     * }
     */

}
