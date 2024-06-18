package it.unimib.sd2024.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import it.unimib.sd2024.beans.Acquisto;
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

@Path("acquisto")
public class AcquistoResource {
    private static Map<String, Acquisto> acquisti = new HashMap<String, Acquisto>();
    static {
        Acquisto a1 = new Acquisto();
        a1.setNumeroCarta("1234567890123456");
        a1.setCvv("123");
        a1.setNomeIntestatario("Mario");
        a1.setCognomeIntestatario("Rossi");
        a1.setDataScadenza(LocalDate.now());

        a1.setCliente("m.rossi@gmail.com");
        acquisti.put("m.rossi@gmail.com", a1);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        if (acquisti != null)
            return Response.ok(acquisti).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }

    @Path("/{mail}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAcquisto(@PathParam("mail") String mail) {
        Acquisto a = acquisti.get(mail);
        if (a != null)
            return Response.ok(a).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAcquisto(Acquisto acquisto) {

        if (acquisti.get(acquisto.getCliente()) == null) {
            acquisti.put(acquisto.getCliente(), acquisto);
            try {
                return Response.created(new URI("http://localhost:8080/acquisto/" + acquisto.getCliente()))
                        .build();
            } catch (URISyntaxException e) {
                return Response.serverError().build();
            }
        } else {
            return Response.status(Status.CONFLICT).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAcquisto(Acquisto acquisto) {
        Acquisto a = acquisti.get(acquisto.getCliente());
        if (a != null) {
            acquisti.put(acquisto.getCliente(), acquisto);
            return Response.ok().build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}