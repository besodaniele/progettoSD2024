package it.unimib.sd2024.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
    private static Map<Integer, Utente> utenti = new HashMap<Integer, Utente>();
    private static int lastId = 0;
    static {
        Utente u1 = new Utente();
        u1.setNome("Mario");
        u1.setCognome("Rossi");
        u1.setEmail("m.rossi@gmail.com");
        u1.setId(lastId++);
        utenti.put(u1.getId(), u1);
        Utente u2 = new Utente();
        u2.setNome("Luigi");
        u2.setCognome("Verdi");
        u2.setEmail("l.verdi@gmail.com");
        u2.setId(lastId++);
        utenti.put(u2.getId(), u2);
    }

    // per testare se viene aggiunto l'utente
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() { 
        if (utenti != null)
            return Response.ok(utenti).build();
        else
            return Response.ok().build();

    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrazione(Utente utente) {
        if (utente.getCognome()==null || utente.getNome()==null || utente.getEmail()==null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        for (Utente u : utenti.values()) {
            if (u.getEmail().equals(utente.getEmail())) {
                return Response.status(Status.CONFLICT).build();
            }
        }
        utente.setId(lastId++);
        utenti.put(utente.getId(), utente);
        try {
            return Response.created(new URI("http://localhost:8080/utente/" + utente.getId())).build();
        } catch (URISyntaxException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Path("/login")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@PathParam("id") int id, @Context HttpServletRequest request) {
        Utente u = utenti.get(id);
        if (u != null) {
            var sessione = request.getSession();
            sessione.setAttribute("utente", u);
            return Response.ok(u).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
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
