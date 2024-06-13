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
@Path("dominio")
public class DominioResource {
    private static Map <String, Dominio> domini=new HashMap<String,Dominio>();

    static{
        Dominio d1=new Dominio();
        d1.setDominio("unimib.it");
        d1.setDataRegistrazione(LocalDate.now());
        d1.setDataScadenza(LocalDate.now());
        domini.put("unimib.it", d1);
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(){
        if( domini!=null)
            return Response.ok(domini).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    } 
    @Path("/{dominio}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDominio(@PathParam("dominio") String dominio){
        Dominio d=domini.get(dominio);
        if(d!=null)
            return Response.ok(d).build();
        else
            return Response.status(Status.NOT_FOUND).build();
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDominio(Dominio dominio){
        
        if(domini.get(dominio.getDominio())==null){
            domini.put(dominio.getDominio(), dominio);
            try{
                return Response.created(new URI("http://localhost:8080/dominio/"+dominio.getDominio())).build();
            }catch(URISyntaxException e){
                return Response.serverError().build();
            }
        }else{
            return Response.status(Status.CONFLICT).build();
        }
    }
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDominio(Dominio dominio){
        Dominio d=domini.get(dominio.getDominio());
        if(d!=null){
            domini.put(dominio.getDominio(), dominio);
            return Response.ok().build();
        }else{
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
