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
            .description("Servicio de usuario")
            .consumes("application/json")
            .produces("application/json")

            .post()
                .consumes("application/x-www-form-urlencoded")
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

            // GET /user
            .get()
                .to("direct:getUsers");
 
   
    }
}
