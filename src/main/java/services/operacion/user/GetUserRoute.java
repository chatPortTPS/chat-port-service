package services.operacion.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetUserRoute extends RouteBuilder {

    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {
        
        from("direct:getUsers")
            .doTry()
                .process(exchange -> {
                    // Lógica del servicio de usuario
                    List<UserResponse> lista = userService.findAllUsers();
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
