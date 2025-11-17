package services.operacion.archivos;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;

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
                .to("direct:getArchivos")

            // GET /archivos/{uuid}
            .get("/{uuid}")
                .param().name("uuid")
                    .type(RestParamType.path)
                    .description("UUID del archivo a obtener")
                    .required(true)
                .endParam()
                .description("Retorna el documento en base 64 y en zip segun UUID")
                .to("direct:getArchivoByUuid");

    }

}
