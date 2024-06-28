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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("dominio")
public class DominioResource {
    @GET
    @Path("lock/{Dominio}")
    public Response setLock(@PathParam("Dominio") String dominio, @QueryParam("id") String id) {
        Connection conn;
        String query = "";
        try {
            conn = new Connection();
            query = "lock domini " + dominio + " " + id;
            conn.send(query);
            String response = conn.receive();
            conn.close();
            if (response.equals("400")) {
                // errore nel database
                return Response.status(Status.BAD_REQUEST).build();
            }
            if (response.equals("409")) {
                // dominio già bloccato
                return Response.status(Status.CONFLICT).build();
            }
            return Response.ok().build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("unlock/{Dominio}")
    public Response setUnlock(@PathParam("Dominio") String dominio, @QueryParam("id") String id) {
        Connection conn;
        String query = "";
        try {
            conn = new Connection();
            query = "unlock domini " + dominio + " " + id;
            conn.send(query);
            String response = conn.receive();
            conn.close();
            if (response.equals("404")) {
                // il lock non esiste
                return Response.status(Status.NOT_FOUND).build();
            }
            if (response.equals("400")) {
                // errore nel database
                return Response.status(Status.BAD_REQUEST).build();
            }
            return Response.ok().build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // restituisce tutti i domini di un utente
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("id") int id) {

        try {
            Connection conn = new Connection();
            conn.send("get domini.*.* where proprietario=" + id);
            String response = conn.receive();
            conn.close();
            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            return Response.ok(response).build();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // disponibilità di un dominio
    @Path("/{dominio}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDominio(@PathParam("dominio") String dominio) {

        Connection conn;
        String query = "get domini.*.* where dominio=" + dominio;
        String finalResponse = "";
        try {
            conn = new Connection();

            conn.send(query);
            String response = conn.receive();

            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            if (response.equals("{}")) {
                // non esiste quel dominio, neanche scaduto
                return Response.status(Status.NOT_FOUND).build();
            }
            String proprietario = null;

            var domini = JsonbBuilder.create().fromJson(response, Map.class);

            for (var k : domini.keySet()) {
                var d = (Map) domini.get(k);

                LocalDate dataScadenza = LocalDate.parse((String) d.get("dataScadenza"));

                if (dataScadenza.isAfter(LocalDate.now())) {
                    // se il dominio non è scaduto mi salvo di chi è
                    finalResponse = finalResponse + JsonbBuilder.create().toJson(d);
                    proprietario = "" + d.get("proprietario");
                }
            }
            if (proprietario == null) {
                // dominio esiste ma è scaduto, quindi è disponibile
                return Response.status(Status.NOT_FOUND).build();
            }

            query = "get utenti." + proprietario + ".*";
            conn.send(query);
            response = conn.receive();
            conn.close();
            if (response.equals("400") || response.equals("404")) {
                // errore nel db o incosistenza, c'è un dominio ma il suo proprietario non
                // esiste
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }

            finalResponse = "["+finalResponse + "," + response+"]";
            return Response.ok(finalResponse).build();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }
    //aggiunge un dominio
    @Path("/{dominio}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDominio(@Context HttpServletRequest request, Acquisto acquisto,
            @PathParam("dominio") String dominio, @QueryParam("id") String id) {
        if (acquisto.getNumAnni() < 1 || acquisto.getNumAnni() > 10) {
            // non si può registrare un dominio per meno di un anno
            return Response.status(Status.FORBIDDEN).build();
        }
        Connection conn;
        String query = "get domini.*.* where dominio=" + dominio;

        try {
            conn = new Connection();
            conn.send(query);
            String response = conn.receive();
            if (response.equals("400")) {
                // errore nel database

                return Response.status(Status.BAD_REQUEST).build();
            }
            var domini = JsonbBuilder.create().fromJson(response, Map.class);
            for (var k : domini.keySet()) {
                var d = (Map) domini.get(k);
                LocalDate dataScadenza = LocalDate.parse((String) d.get("dataScadenza"));
                if (dataScadenza.isAfter(LocalDate.now())) {
                    // dominio già registrato e non scaduto
                    return Response.status(Status.CONFLICT).build();
                }
            }

            Dominio d = new Dominio();
            d.setDominio(dominio);
            conn.send("getLastIndex domini");
            int lastId = Integer.parseInt(conn.receive()) + 1;
            d.setId("" + lastId);
            d.setProprietario(id);
            d.setDataRegistrazione(LocalDate.now());
            d.setDataScadenza(d.getDataRegistrazione().plusYears(acquisto.getNumAnni()));
            conn.send("insert domini " + lastId + " " + d.getDominio() + " " + id + " "
                    + JsonbBuilder.create().toJson(d));
            response = conn.receive();
            if (response.equals("409")) {
                // dominio già registrato o già lockato
                return Response.status(Status.CONFLICT).build();
            } else if (response.equals("400")) {
                // dominio non valido
                return Response.status(Status.BAD_REQUEST).build();
            }

            conn.send("getLastIndex acquisti");
            int lastIdAcquisto = Integer.parseInt(conn.receive()) + 1;
            acquisto.setCliente(id);
            acquisto.setId("" + lastIdAcquisto);
            acquisto.setDataOrdine(LocalDate.now());
            acquisto.setDominio(dominio);
            acquisto.setTipo("acquisto");
            acquisto.setQuota(acquisto.getNumAnni() * 10); // applico tariffa fissa di 10 euro all'anno per l'acquisto
                                                           // di un dominio
            conn.send("lock acquisti " + acquisto.getId() + " " + id);
            conn.send("insert acquisti " + acquisto.getId() + " " + acquisto.getId() + " " + id + " "
                    + JsonbBuilder.create().toJson(acquisto));
            response = conn.receive();

            conn.send("unlock acquisti " + acquisto.getId() + " " + id);
            query = "unlock domini " + dominio + " " + id;
            conn.send(query);
            response = conn.receive();
            conn.close();
            if (response.equals("404")) {
                // il dominio non esiste
                return Response.status(Status.NOT_FOUND).build();
            }
            if (response.equals("400")) {
                // errore nel database

                return Response.status(Status.BAD_REQUEST).build();
            }

            conn.close();

            return Response.created(new URI("http://localhost:8080/dominio/" + dominio)).build();

        } catch (IOException | URISyntaxException e) {

            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }
    // rinnovo di un dominio
    @Path("/{dominio}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDominio(@QueryParam("id") String id, @PathParam("dominio") String dominio,
            Acquisto acquisto) {
        Connection conn;
        if (acquisto.getNumAnni() < 1) {
            // non si può rinnovare un dominio per meno di un anno
            return Response.status(Status.FORBIDDEN).build();
        }
        String query = "get domini.*.* where dominio=" + dominio;
        try {
            conn = new Connection();
            conn.send(query);
            String response = conn.receive();
            if (response.equals("400")) {
                // errore nel database
                return Response.status(Status.BAD_REQUEST).build();
            }
            var domini = JsonbBuilder.create().fromJson(response, Map.class);
            Map dominioObject = null;

            for (var k : domini.keySet()) {
                var d = (Map) domini.get(k);
                LocalDate dataScadenza = LocalDate.parse((String) d.get("dataScadenza"));

                String idDom = d.get("proprietario").toString();

                if (id.equals(idDom)
                        && dataScadenza.isAfter(LocalDate.now())) {
                    // dominio trovato, non è ancora scaduto e appartiene all'utente
                    dominioObject = d;
                }

            }
            if (dominioObject == null) {
                // dominio non trovato
                return Response.status(Status.NOT_FOUND).build();
            }
            LocalDate dataScadenza = LocalDate.parse((String) dominioObject.get("dataScadenza"));
            int anniMancanti = dataScadenza.getYear() - LocalDate.now().getYear();
            if (anniMancanti + acquisto.getNumAnni() > 10) {
                // non si può rinnovare un dominio per più di 10 anni totali
                return Response.status(Status.FORBIDDEN).build();
            }

            Dominio d = new Dominio();
            d.setDominio(dominioObject.get("dominio").toString());
            d.setId(dominioObject.get("id").toString());
            d.setProprietario(dominioObject.get("proprietario").toString());
            d.setDataRegistrazione(LocalDate.parse(dominioObject.get("dataRegistrazione").toString()));
            d.setDataScadenza(
                    LocalDate.parse(dominioObject.get("dataScadenza").toString()).plusYears(acquisto.getNumAnni()));
            conn.send("update domini " + d.getId() + " " + d.getDominio() + " " + id + " " + JsonbBuilder.create().toJson(d));

            response = conn.receive();
            if (response.equals("409")) {
                // dominio già registrato
                return Response.status(Status.CONFLICT).build();
            } else if (response.equals("400")) {
                // dominio non valido
                return Response.status(Status.BAD_REQUEST).build();
            }

            conn.send("getLastIndex acquisti");
            int lastIdAcquisto = Integer.parseInt(conn.receive()) + 1;
            acquisto.setCliente(id);
            acquisto.setId("" + lastIdAcquisto);
            acquisto.setDataOrdine(LocalDate.now());

            acquisto.setDominio(dominio);
            acquisto.setTipo("rinnovo");
            acquisto.setQuota(acquisto.getNumAnni() * 5);
            conn.send("lock acquisti " + acquisto.getId() + " " + id);

            conn.send("insert acquisti " + acquisto.getId() + " "+ acquisto.getId() +" " + id + " " + JsonbBuilder.create().toJson(acquisto));
            response = conn.receive();
            if (response.equals("409")) {
                // acquisto già registrato, non dovrebbe mai succedere
                return Response.status(Status.CONFLICT).build();
            } else if (response.equals("400")) {
                // formato acquisto non valido
                return Response.status(Status.BAD_REQUEST).build();
            }
            conn.send("unlock acquisti " + acquisto.getId() + " " + id);

            query = "unlock domini " + dominio + " " + id;
            conn.send(query);
            response = conn.receive();
            conn.close();
            if (response.equals("404")) {
                // il dominio non esiste
                return Response.status(Status.NOT_FOUND).build();
            }
            if (response.equals("400")) {
                // errore nel database
                return Response.status(Status.BAD_REQUEST).build();
            }

            conn.close();

            return Response.created(new URI("http://localhost:8080/dominio/" + dominio)).build();

        } catch (IOException | URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }
}
