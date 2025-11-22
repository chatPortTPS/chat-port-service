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
                .to("direct:getArchivoByUuid")

            .post()
                .consumes("multipart/form-data")
                .bindingMode(org.apache.camel.model.rest.RestBindingMode.off)
                .description("Subir un nuevo archivo")
                .param().name("file")
                    .type(RestParamType.formData)
                    .description("Archivo a subir")
                    .dataType("object")
                    .required(true)
                .endParam()
                .param().name("nombre")
                    .type(RestParamType.formData)
                    .description("Nombre del archivo")
                    .dataType("string")
                    .required(true)
                .endParam()
                .param().name("correo")
                    .type(RestParamType.formData)
                    .description("Correo asociado al archivo")
                    .dataType("string")
                    .required(true)
                .endParam()
                .param().name("autor")
                    .type(RestParamType.formData)
                    .description("Autor del archivo")
                    .dataType("string")
                    .required(true)
                .endParam()
                .param().name("agenteId")
                    .type(RestParamType.formData)
                    .description("ID del agente asociado al archivo")
                    .dataType("string")
                    .required(true)
                .endParam()
                .to("direct:upload");

    }

}
