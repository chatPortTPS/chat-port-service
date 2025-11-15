package services.operacion.archivos;


import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;

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
                .description("Obtener lista de archivos indexados")
                .to("direct:getArchivos");

    }

}
