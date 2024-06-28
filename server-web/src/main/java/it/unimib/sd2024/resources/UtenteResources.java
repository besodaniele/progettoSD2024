package it.unimib.sd2024.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import it.unimib.sd2024.Connection;
import it.unimib.sd2024.beans.Utente;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("utente")
public class UtenteResources {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrazione(Utente utente) {
        try {
            // Ottiene una connessione al database
            Connection conn = new Connection();

            // Invia una richiesta al database per ottenere l'ultimo indice degli utenti
            conn.send("getLastIndex utenti");
            String response = conn.receive();

            // Controlla se la risposta indica un errore nel database
            if (response.equals("400") || response.equals("404")) {
                // Errore nel database, restituisce una risposta con stato BAD_REQUEST
                return Response.status(Status.BAD_REQUEST).build();
            }

            // Calcola il nuovo ID per l'utente
            int lastID = (Integer.parseInt(response));
            lastID++;

            // Invia una richiesta al database per bloccare l'ID appena calcolato
            conn.send("lock utenti " + lastID + " " + lastID);
            response = conn.receive();

            // Controlla se la risposta indica un errore nel database
            if (response.equals("400")) {
                // Errore nel database, restituisce una risposta con stato BAD_REQUEST
                return Response.status(Status.BAD_REQUEST).build();
            }

            // Controlla se la risposta indica che l'utente è già bloccato
            if (response.equals("409")) {
                // Utente già bloccato, restituisce una risposta con stato CONFLICT
                return Response.status(Status.CONFLICT).build();
            }

            // Invia una richiesta al database per verificare se esiste già un utente con la
            // stessa email
            conn.send("get utenti.*.* where email=" + utente.getEmail());
            response = conn.receive();

            // Controlla se la risposta indica che esiste già un utente con la stessa email
            if (!response.equals("{}")) {
                // Già presente un utente con la stessa email, sblocca l'ID e restituisce una
                // risposta con stato CONFLICT
                conn.send("unlock utenti " + lastID + " " + lastID);
                conn.close();
                return Response.status(Status.CONFLICT).build();
            }

            // Assegna il nuovo ID all'utente
            utente.setId(lastID);

            // Invia una richiesta al database per inserire l'utente
            conn.send("insert utenti " + lastID + " " + utente.getId() + " " + utente.getId() + " "
                    + JsonbBuilder.create().toJson(utente));
            response = conn.receive();

            // Controlla se la risposta indica un errore nel database
            if (response.equals("400")) {
                // Errore nel database, restituisce una risposta con stato BAD_REQUEST
                return Response.status(Status.BAD_REQUEST).build();
            }

            // Controlla se la risposta indica che c'è un conflitto nell'inserimento
            // dell'utente
            if (response.equals("409")) {
                // Conflitto nell'inserimento dell'utente, restituisce una risposta con stato
                // CONFLICT
                return Response.status(Status.CONFLICT).build();
            }

            // Sblocca l'ID e chiude la connessione al database
            conn.send("unlock utenti " + lastID + " " + lastID);
            conn.close();

            // Restituisce una risposta con stato CREATED e l'URI dell'utente appena creato
            return Response.created(new URI("http://localhost:8080/utente/" + utente.getId())).build();
        } catch (JsonbException | IOException | URISyntaxException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Path("/login/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@PathParam("id") int id) {

        try {
            Connection conn = new Connection();
            conn.send("get utenti." + id + ".*");
            String response = conn.receive();

            // Se la risposta è un oggetto JSON vuoto, significa che l'utente non è stato
            // trovato
            if (response.equals("{}")) {
                return Response.status(Status.NOT_FOUND).build();
            }
            // Se la risposta è "400", significa che c'è stato un errore nella richiesta
            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            conn.close();

            // Deserializza la risposta JSON in un oggetto Utente utilizzando Jsonb
            Utente u = JsonbBuilder.create().fromJson(response, Utente.class);

            // Restituisce una risposta di successo con l'oggetto Utente
            return Response.ok(u).build();

        } catch (JsonbException | IOException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
