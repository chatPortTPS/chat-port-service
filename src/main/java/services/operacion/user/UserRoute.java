package services.operacion.user;

import org.apache.camel.builder.RouteBuilder; 
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
                .type(String.class)
                .to("direct:creteUser")

            // GET /user
            .get()
                .to("direct:user");
 
   
    }
}
