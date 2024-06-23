package it.unimib.sd2024.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unimib.sd2024.Connection;
import it.unimib.sd2024.beans.Acquisto;
import it.unimib.sd2024.beans.Dominio;
import it.unimib.sd2024.beans.Utente;
import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("dominio")
public class DominioResource {
    private static Map<Integer, Dominio> domini = new HashMap<Integer, Dominio>();
    private static int lastId = 0;
    static {
        Dominio d1 = new Dominio();
        d1.setDominio("unimib.it");
        d1.setDataRegistrazione(LocalDate.now());
        d1.setDataScadenza(LocalDate.of(2026, 1, 1));

        d1.setProprietario(0);
        d1.setId(lastId++);

        Dominio d2 = new Dominio();
        d2.setDominio("google.com");
        d2.setDataRegistrazione(LocalDate.now());
        d2.setDataScadenza(LocalDate.of(2025, 1, 1));

        d2.setProprietario(1);
        d2.setId(lastId++);
        domini.put(d2.getId(), d2);
        domini.put(d1.getId(), d1);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest request) {
        Utente u = (Utente) request.getSession().getAttribute("utente");
        if (u == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        List<Dominio> currentDomains = new ArrayList<Dominio>();
        if (domini != null) {

            for (Dominio d : domini.values()) {
                if (d.getProprietario() == u.getId()) {
                    currentDomains.add(d);
                }
            }
            return Response.ok(currentDomains).build();
        } else
            return Response.ok().build();
    }

    // ricerca di un dominio
    @Path("/{dominio}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDominio(@PathParam("dominio") String dominio) {
        Dominio d = domini.get(dominio);
        if (d != null)
            return Response.ok(d).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }

    @Path("/{dominio}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    // dati acquisti come query param???
    public Response addDominio(@Context HttpServletRequest request, Acquisto acquisto,
            @PathParam("dominio") String dominio) {
        // check if user logged in
        Utente u = (Utente) request.getSession().getAttribute("utente");
        if (u == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        // sto effettuando un acquisto -> aggiungo anche l'acquisto al db
        acquisto.setCognome(u.getCognome());
        acquisto.setMail(u.getEmail());
        acquisto.setNome(u.getNome());
        acquisto.setTipo("acquisto");
        // a.setId(lastIdAcquisto++);

        // check if domain already exists
        for (Dominio d : domini.values()) {
            if (d.getDominio().equals(dominio) && d.getDataScadenza().isAfter(LocalDate.now())) {
                return Response.status(Status.CONFLICT).build();
            }
        }

        Dominio d = new Dominio();
        d.setDominio(dominio);
        d.setId(lastId++);
        d.setProprietario(u.getId());
        d.setDataRegistrazione(LocalDate.now());
        d.setDataScadenza(d.getDataRegistrazione().plusYears(acquisto.getNumAnni()));
        domini.put(d.getId(), d);
        // acquisti.put(a.getId(), a);
        return Response.ok().build();

    }

    // rinnovo di un dominio
    @Path("/{dominio}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDominio(@Context HttpServletRequest request, @PathParam("dominio") String dominio,
            Acquisto acquisto) {
        /*
         * logica di update con db
         * prima chiedo risorse al db
         * la modifico lato server e chiedo al db di aggiornare
         * il db semplicemente rimuove la vecchia risorse e inserisce la nuova
         */
        Dominio d = null;
        for (Dominio x : domini.values()) {
            if (x.getDominio().equals(dominio)) {
                d = x;
            }
        }

        if (d == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        Utente u = (Utente) request.getSession().getAttribute("utente");
        if (u == null || d.getProprietario() != u.getId()) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        acquisto.setNome(u.getNome());
        acquisto.setCognome(u.getCognome());
        acquisto.setMail(u.getEmail());
        acquisto.setTipo("rinnovo");
        // a.setId(lastIdAcquisto++);
        int anniMancanti = d.getDataScadenza().getYear() - LocalDate.now().getYear();

        if (!d.getDataScadenza().isBefore(LocalDate.now()) &&
                anniMancanti + acquisto.getNumAnni() <= 10) {
            // inserisco acquisto nel db
            // acquisti.put(a.getId(), a);
            d.setDataScadenza(d.getDataScadenza().plusYears(acquisto.getNumAnni()));
            // agiorno il dominio nel db
            domini.put(d.getId(), d);
            return Response.ok().build();
        } else {
            return Response.status(Status.UNAUTHORIZED).build();
        }

    }
}
