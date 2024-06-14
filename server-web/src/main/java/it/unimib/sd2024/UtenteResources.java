package it.unimib.sd2024;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import jakarta.json.JsonException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("utente")
public class UtenteResources {
    private static Map<String, Utente> utenti = new HashMap<String, Utente>();
    static {
        Utente u1 = new Utente();
        u1.setNome("Mario");
        u1.setCognome("Rossi");
        u1.setEmail("prova@gmail.com");
        u1.setPassword("1234");
        utenti.put("prova@gmail.com", u1);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        if (utenti != null)
            return Response.ok(utenti).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }

    @Path("/{email}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUtente(@PathParam("email") String email) {
        Utente u = utenti.get(email);
        if (u != null)
            return Response.ok(u).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUtente(Utente utente) {

        if (utenti.get(utente.getEmail()) == null) {
            utenti.put(utente.getEmail(), utente);
            try {
                return Response.created(new URI("http://localhost:8080/utente/" + utente.getEmail())).build();
            } catch (URISyntaxException e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return Response.status(Status.CONFLICT).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUtente(Utente utente) {
        Utente u = utenti.get(utente.getEmail());
        if (u != null) {
            utenti.put(utente.getEmail(), utente);
            return Response.ok().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

}
