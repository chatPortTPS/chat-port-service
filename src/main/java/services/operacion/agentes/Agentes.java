package services.operacion.agentes;

import org.apache.camel.builder.RouteBuilder;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Agentes extends RouteBuilder {
 
    @Override
    public void configure() throws Exception {

        rest("/agentes")
            .description("Servicio para gestionar los agentes conversacionales")
            .consumes("application/json")
            .produces("application/json")

            // GET /agentes
            .get()
                .to("direct:agentes");

        
        from("direct:agentes")
            .log("Procesando solicitud para /agentes")
            .setBody(constant("Lista de agentes administrativos"))
            .log("Respuesta generada: ${body}");


    }

}
