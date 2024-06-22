package it.unimib.sd2024.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.unimib.sd2024.beans.Acquisto;
import it.unimib.sd2024.beans.Utente;
import jakarta.json.JsonException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
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
        a1.setQuota(100.0);

        a1.setCliente(0);
        acquisti.put("m.rossi@gmail.com", a1);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest request) {
        Utente u =(Utente) request.getSession().getAttribute("utente");

        if (u == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        List <Acquisto> currentAcquisti= new ArrayList<Acquisto>();
        if (acquisti != null)
        {
            for (Acquisto a : acquisti.values())
            {
                if (a.getCliente()==(u.getId()))
                {
                    currentAcquisti.add(a);
                }
            }
            return Response.ok(currentAcquisti).build();
        }
        else
            return Response.status(Status.NOT_FOUND).build();
    }


}