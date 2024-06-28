package it.unimib.sd2024.resources;

import java.io.IOException;
import it.unimib.sd2024.Connection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("acquisto")
public class AcquistoResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("id") int id) {

        Connection conn;
        try {
            // Tentativo di stabilire una connessione
            conn = new Connection();
            // Invio della richiesta al server per ottenere gli acquisti del cliente
            // specificato dall'ID
            conn.send("get acquisti.*.* where cliente=" + id);
            // Ricezione della risposta dal server
            String response = conn.receive();
            // Chiusura della connessione
            conn.close();

            // Controllo se la risposta è "400", che indica una richiesta errata
            if (response.equals("400")) {
                // Restituzione di una risposta HTTP con stato 400 BAD REQUEST
                return Response.status(Status.BAD_REQUEST).build();
            }
            // Restituzione della risposta del server con stato 200 OK
            return Response.ok(response).build();

            // Gestione delle eccezioni di I/O
        } catch (IOException e) {
            // Restituzione di una risposta HTTP con stato 500 INTERNAL SERVER ERROR
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}