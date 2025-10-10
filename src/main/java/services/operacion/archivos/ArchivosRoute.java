package services.operacion.archivos;


import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class ArchivosRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        rest("/archivos")
            .description("Servicio para gestion de archivos indexados y disponibles en para preguntas")
            .consumes("application/json")
            .produces("application/json")
        
            // GET /archivos
            .get()
                .to("direct:getArchivos");

    }

}
