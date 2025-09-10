package services.operacion.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.UserService;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class IsAdminRoute  extends RouteBuilder {

    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {
       
        from("direct:isAdmin")
            .doTry()
                .log("Checking if user is admin")
                .process(exchange -> {
                    
                    String username = exchange.getIn().getHeader("username", String.class);

                    if (username == null || username.trim().isEmpty()) {
                        throw new IllegalArgumentException("Username es requerido");
                    }

                    List<UserResponse> users = new ArrayList<>(); 
                    users.add(userService.getUserByUsername(username));

                    exchange.getIn().setHeader("message", "Usuario Activo");
                    exchange.getIn().setBody(users);
                })
                .to("direct:success")
            .doCatch(Exception.class) 
                .to("direct:error");
    }

}
