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
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest request) {
        Utente u = (Utente) request.getSession().getAttribute("utente");

        if (u == null) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        Connection conn;
        try {
            conn = new Connection();
            conn.send("get acquisti.*.* where cliente=" + u.getId());
            String response = conn.receive();
            conn.close();

            if (response.equals("400")) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            return Response.ok(response).build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

    }

}