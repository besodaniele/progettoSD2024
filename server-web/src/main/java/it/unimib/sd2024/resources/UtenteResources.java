package it.unimib.sd2024.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import it.unimib.sd2024.Connection;
import it.unimib.sd2024.beans.Utente;
import jakarta.json.JsonException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("utente")
public class UtenteResources {

    // per testare se viene aggiunto l'utente
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Connection conn;
        try {
            conn = new Connection();
            conn.send("get utenti.*.*");
            String response = conn.receive();
            conn.close();
            return Response.ok(response).build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrazione(Utente utente) {
        try {
            Connection conn = new Connection();
            conn.send("getLastIndex utenti");
            int lastID = (Integer.parseInt(conn.receive()));
            lastID++;
            conn.send("lock utenti " + lastID + " " + lastID);
            String response = conn.receive();
            if (response.equals("400")) {
                // errore nel database
                return Response.status(Status.BAD_REQUEST).build();
            }
            if (response.equals("409")) {
                // utente già bloccato
                return Response.status(Status.CONFLICT).build();
            }
            conn.send("get utenti.*.* where email=" + utente.getEmail());
            response = conn.receive();
            System.out.println(response);
            if (!response.equals("{}")) {


                //già presente un utente con la stessa email
                conn.send("unlock utenti " + lastID + " " + lastID);
                conn.close();
                return Response.status(Status.CONFLICT).build();
            }
            utente.setId(lastID);
            conn.send("insert utenti " + lastID + " " + utente.getId() + " " + utente.getId() + " "
                    + JsonbBuilder.create().toJson(utente));
            response = conn.receive();
            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            if (response.equals("409")) {
                return Response.status(Status.CONFLICT).build();
            }
            conn.send("unlock utenti " + lastID + " " + lastID);
            conn.close();
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

            if (response.equals("404")) {
                return Response.status(Status.NOT_FOUND).build();
            }
            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            conn.close();

            System.out.println(response);

            Utente u = JsonbBuilder.create().fromJson(response, Utente.class);

            return Response.ok(u).build();

        } catch (JsonbException | IOException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
