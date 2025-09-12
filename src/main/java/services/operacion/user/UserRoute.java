package services.operacion.user;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRoute extends RouteBuilder {
 
    @Override
    public void configure() throws Exception {
        // Definición de un endpoint REST
        rest("/user")
            .description("Servicio para usuario administrativo del sistema")
            .consumes("application/json")
            .produces("application/json")

            // GET /user/dashboard
            .get("/dashboard")
                .to("direct:dashboard")

            
            .get("/isAdmin")
                .consumes("application/json")
                .produces("application/json")
                .param()
                    .name("username")
                    .type(RestParamType.query)
                    .dataType("string")
                    .required(true)
                .endParam()
                .to("direct:isAdmin")

             
            // POST /user
            .post()
                .consumes("application/json")
                .produces("application/json")
                .param()
                    .name("username")
                    .type(RestParamType.query)
                    .dataType("string")
                    .required(true)
                .endParam()
                .param()
                    .name("email")
                    .type(RestParamType.query)
                    .dataType("string")
                    .required(true)
                .endParam()
                .to("direct:createUser")



            // POST /user/desactivate
            .post("/desactivate")
                .consumes("application/json")
                .produces("application/json")
                .param()
                    .name("id")
                    .type(RestParamType.query)
                    .dataType("string")
                    .required(true)
                .endParam()
                .to("direct:deactivateUser")


            // DELETE /user
            .delete()
                .consumes("application/json")
                .produces("application/json")
                .param()
                    .name("id")
                    .type(RestParamType.query)
                    .dataType("string")
                    .required(true)
                .endParam()
                .to("direct:deleteUser")

            
            .post("/income")
                .consumes("application/json")
                .produces("application/json")
                .param()
                    .name("username")
                    .type(RestParamType.query)
                    .dataType("string")
                    .required(true)
                .endParam()
                .to("direct:incomeUser")
 

            // GET /user
            .get()
                .param().name("page")
                    .type(RestParamType.query)
                    .defaultValue("0")
                    .description("Número de página")
                .endParam()
                .param().name("size")
                    .type(RestParamType.query)
                    .required(false)
                    .defaultValue("10")
                    .description("Tamaño de página")
                    .endParam()
                .param().name("sortBy")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Campo para ordenar")
                    .endParam()
                .param().name("sortDirection")
                    .type(RestParamType.query) 
                    .defaultValue("ASC")
                    .description("Dirección de ordenamiento")
                    .required(false)
                    .endParam()
                .param().name("filter")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Filtro de búsqueda por el username o email")
                    .endParam()
                .to("direct:getUsers");
 
   
    }
}
