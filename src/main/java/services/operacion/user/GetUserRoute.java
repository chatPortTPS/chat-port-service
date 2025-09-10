package services.operacion.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder; 
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetUserRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        from("direct:getUsers")
            .doTry()
                .process(exchange -> {
                    // Lógica del servicio de usuario
                    List<UserResponse> lista = new ArrayList<>();
                    lista.add(new UserResponse("Usuario 1", "usuario1@example.com"));
                    lista.add(new UserResponse("Usuario 2", "usuario2@example.com"));

                    exchange.getMessage().setBody(lista);
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .process(exchange -> {
                    // Manejo de excepciones
                    List<String> lista = new ArrayList<>();
                    exchange.getMessage().setBody(lista);
                })
                .to("direct:error")
        .end();
        
    }
}
