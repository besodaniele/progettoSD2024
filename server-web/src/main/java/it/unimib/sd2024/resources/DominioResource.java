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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("dominio")
public class DominioResource {

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

    // ricerca di un dominio
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
            int proprietario = -1;

            var domini = JsonbBuilder.create().fromJson(response, Map.class);

            for (var k : domini.keySet()) {
                var d = (Map) domini.get(k);
                LocalDate dataScadenza = LocalDate.parse((String) d.get("dataScadenza"));
                if (dataScadenza.isAfter(LocalDate.now())) {
                    finalResponse = finalResponse + JsonbBuilder.create().toJson(d);
                    proprietario = Integer.parseInt("" + d.get("proprietario"));
                }
            }
            if (proprietario == -1) {
                return Response.status(Status.NOT_FOUND).build();
            }

            query = "get utenti." + proprietario + ".*";
            conn.send(query);
            response = conn.receive();
            conn.close();
            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            finalResponse = finalResponse + "," + response;
            return Response.ok(finalResponse).build();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Path("/{dominio}")

    @POST

    @Consumes(MediaType.APPLICATION_JSON)

    @Produces(MediaType.APPLICATION_JSON)
    public Response addDominio(@Context HttpServletRequest request, Acquisto acquisto,
            @PathParam("dominio") String dominio, @QueryParam("id") int id) {

        Connection conn;
        String query = "get domini.*.* where dominio=" + dominio;

        try {
            conn = new Connection();
            conn.send(query);
            String response = conn.receive();
            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            var domini = JsonbBuilder.create().fromJson(response, Map.class);
            for (var k : domini.keySet()) {
                var d = (Map) domini.get(k);
                LocalDate dataScadenza = LocalDate.parse((String) d.get("dataScadenza"));
                if (dataScadenza.isAfter(LocalDate.now())) {
                    System.out.println("Dominio già registrato e non scaduto");
                    return Response.status(Status.CONFLICT).build();
                }
            }

            Dominio d = new Dominio();
            d.setDominio(dominio);
            conn.send("getLastIndex domini");
            int lastId = Integer.parseInt(conn.receive()) + 1;
            d.setId(lastId);
            d.setProprietario(id);
            d.setDataRegistrazione(LocalDate.now());
            d.setDataScadenza(d.getDataRegistrazione().plusYears(acquisto.getNumAnni()));
            conn.send("insert domini " + lastId + " " + JsonbBuilder.create().toJson(d));
            response = conn.receive();
            if (response.equals("409")) {
                System.out.println("Dominio già registrato");
                return Response.status(Status.CONFLICT).build();
            } else if (response.equals("400")) {
                System.out.println("Dominio non valido");
                return Response.status(Status.BAD_REQUEST).build();
            }

            conn.send("getLastIndex acquisti");
            int lastIdAcquisto = Integer.parseInt(conn.receive()) + 1;
            acquisto.setCliente(id);
            acquisto.setId(lastIdAcquisto);
            acquisto.setDominio(dominio);
            acquisto.setTipo("rinnovo");
            conn.send("insert acquisti " + acquisto.getId() + " " + JsonbBuilder.create().toJson(acquisto));
            response = conn.receive();
            if (response.equals("409")) {
                System.out.println("Acquisto già registrato");
                return Response.status(Status.CONFLICT).build();
            } else if (response.equals("400")) {
                System.out.println("Acquisto non valido");
                return Response.status(Status.BAD_REQUEST).build();
            }
            conn.close();

            return Response.created(new URI("http://localhost:8080/dominio/" + dominio)).build();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    // rinnovo di un dominio

    @Path("/{dominio}")

    @PUT

    @Consumes(MediaType.APPLICATION_JSON)

    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDominio(@QueryParam ("id") int id, @PathParam("dominio") String dominio,
            Acquisto acquisto) {
        Connection conn;
        System.out.println();
        String query = "get domini.*.* where dominio=" + dominio;
        try {
            conn = new Connection();
            conn.send(query);
            String response = conn.receive();
            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            var domini = JsonbBuilder.create().fromJson(response, Map.class);
            Map dominioObject = null;

            for (var k : domini.keySet()) {
                var d = (Map) domini.get(k);
                LocalDate dataScadenza = LocalDate.parse((String) d.get("dataScadenza"));

                int idDom = Integer.parseInt(d.get("proprietario").toString());

                if (id == idDom
                        && dataScadenza.isAfter(LocalDate.now())) {
                    dominioObject = d;
                }

            }
            if (dominioObject == null) {
                System.out.println("Dominio non trovato");
                return Response.status(Status.NOT_FOUND).build();
            }
            LocalDate dataScadenza = LocalDate.parse((String) dominioObject.get("dataScadenza"));
            int anniMancanti = dataScadenza.getYear() - LocalDate.now().getYear();
            if (anniMancanti + acquisto.getNumAnni() > 10) {
                System.out.println("Rinnovo non valido");
                return Response.status(Status.UNAUTHORIZED).build();
            }

            Dominio d = new Dominio();
            d.setDominio(dominioObject.get("dominio").toString());
            d.setId(Integer.parseInt(dominioObject.get("id").toString()));
            d.setProprietario(Integer.parseInt(dominioObject.get("proprietario").toString()));
            d.setDataRegistrazione(LocalDate.parse(dominioObject.get("dataRegistrazione").toString()));
            d.setDataScadenza(
                    LocalDate.parse(dominioObject.get("dataScadenza").toString()).plusYears(acquisto.getNumAnni()));
            conn.send("update domini " + d.getId() + " " + JsonbBuilder.create().toJson(d));

            response = conn.receive();
            if (response.equals("409")) {
                System.out.println("Dominio già registrato");
                return Response.status(Status.CONFLICT).build();
            } else if (response.equals("400")) {
                System.out.println("Dominio non valido");
                return Response.status(Status.BAD_REQUEST).build();
            }

            conn.send("getLastIndex acquisti");
            int lastIdAcquisto = Integer.parseInt(conn.receive()) + 1;
            acquisto.setCliente(id);
            acquisto.setId(lastIdAcquisto);
            acquisto.setDominio(dominio);
            acquisto.setTipo("rinnovo");
            conn.send("insert acquisti " + acquisto.getId() + " " + JsonbBuilder.create().toJson(acquisto));
            response = conn.receive();
            if (response.equals("409")) {
                System.out.println("Acquisto già registrato");
                return Response.status(Status.CONFLICT).build();
            } else if (response.equals("400")) {
                System.out.println("Acquisto non valido");
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

    @GET
    @Path("/testDB")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response testDB() {
        try {
            Connection conn = new Connection();
            conn.send("get domini.*.*");
            String response = conn.receive();
            conn.close();
            return Response.ok(response).build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
