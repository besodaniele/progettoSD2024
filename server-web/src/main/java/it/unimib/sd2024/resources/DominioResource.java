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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //dati acquisti come query param???
    public Response addDominio(@Context HttpServletRequest request) {
        // check if user logged in
        Utente u = (Utente) request.getSession().getAttribute("utente");
        if (u == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        //sto effettuando un acquisto -> aggiungo anche l'acquisto al db
        String dominio = request.getParameter("dominio");
        String cvv = request.getParameter("cvv");
        String nomeIntestatario = request.getParameter("nomeIntestatario");
        String cognomeIntestatario = request.getParameter("cognomeIntestatario");
        String numeroCarta = request.getParameter("numeroCarta");
        LocalDate dataScadenza = LocalDate.parse(request.getParameter("dataScadenza"));
        double quota = Double.parseDouble(request.getParameter("quota"));
        Acquisto a = new Acquisto();
        a.setNome(u.getNome());
        a.setCognome(u.getCognome());   
        a.setMail(u.getEmail());
        a.setCvv(cvv);
        a.setDataScadenza(dataScadenza);
        a.setNomeIntestatario(nomeIntestatario);    
        a.setCognomeIntestatario(cognomeIntestatario);
        a.setNumeroCarta(numeroCarta);
        a.setQuota(quota);
        //a.setId(lastIdAcquisto++);

        // check if domain already exists
        for (Dominio d : domini.values()) {
            if (d.getDominio().equals(dominio) && d.getDataScadenza().isAfter(LocalDate.now()) ) {
                return Response.status(Status.CONFLICT).build();
            }
        }

        Dominio d = new Dominio();
        d.setId(lastId++);
        d.setProprietario(u.getId());
        d.setDataRegistrazione(LocalDate.now());
        domini.put(d.getId(), d);
        //acquisti.put(a.getId(), a);
        return Response.ok().build();

    }

    // rinnovo di un dominio
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDominio(@Context HttpServletRequest request, Dominio dominio) {
        //TODO modificare
        Dominio d = domini.get(dominio.getId());




        if (d == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        Utente u = (Utente) request.getSession().getAttribute("utente");
        if (u == null || d.getProprietario() != u.getId()) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        if (!d.getDataScadenza().isBefore(LocalDate.now())) {
            // inserisco acquisto nel db

            // agiorno il dominio nel db
            domini.put(dominio.getId(), dominio);
            return Response.ok().build();
        } else {
            return Response.status(Status.UNAUTHORIZED).build();
        }

    }
    @Path("/testDB")
    @GET
    public Response testDB() {
        try {
            Connection conn = new Connection();
            return Response.ok().build();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
    }
}
