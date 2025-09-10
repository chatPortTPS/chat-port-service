package services.operacion.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder; 

import com.tps.orm.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateUserRoute extends RouteBuilder {

    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {
        
        from("direct:createUser")
            .doTry()
                .log(LoggingLevel.ERROR, "Creando usuario: ${header.username} con el email: ${header.email}")
                .process(exchange -> {
                    // Lógica para crear un usuario
                    
                    String username = exchange.getIn().getHeader("username", String.class);
                    String email = exchange.getIn().getHeader("email", String.class);
                    
                    UserResponse user = userService.createUser(username, email);

                    List<UserResponse> users = new ArrayList<>();
                    users.add(user);

                    exchange.getMessage().setBody(users);

                })
                .to("direct:success")
            .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Error al crear usuario: ${exception.message}")
                .to("direct:error")
            .endDoTry()
        .end();

        
    }
}
