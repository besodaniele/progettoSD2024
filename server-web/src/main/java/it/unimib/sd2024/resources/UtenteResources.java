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
        try {
            Connection conn = new Connection();
            conn.send("get utenti.*.*");
            String response = conn.receive();

            conn.close();
            return Response.ok(response).build();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrazione(Utente utente) {
        if (utente.getCognome() == null || utente.getNome() == null || utente.getEmail() == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        try {
            Connection conn = new Connection();
            conn.send("getLastIndex utenti");
            int id = Integer.parseInt(conn.receive()) + 1;
            utente.setId(id);
            conn.send("insert utenti " + id + " " + JsonbBuilder.create().toJson(utente));
            String response = conn.receive();
            if (response.equals("409")) {
                return Response.status(Status.CONFLICT).build();
            } else if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            conn.close();
            return Response.created(new URI("http://localhost:8080/utente/" + utente.getId())).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("/login/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@PathParam("id") int id, @Context HttpServletRequest request) {

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

            // attendere correzione get oggetto singolo
            Utente u = JsonbBuilder.create().fromJson(response, Utente.class);
            
            if (u != null) {
                var sessione = request.getSession();
                sessione.setAttribute("utente", u);
                return Response.ok(u).build();
            } else {
                return Response.status(Status.NOT_FOUND).build();
            }
        } catch (JsonbException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // test per visionare l'utente loggato
    @Path("testLogin")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response testLogin(@Context HttpServletRequest request) {
        HttpSession sessione = request.getSession();
        Utente u = (Utente) sessione.getAttribute("utente");
        if (u != null) {
            return Response.ok(u).build();
        } else {
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }

}
