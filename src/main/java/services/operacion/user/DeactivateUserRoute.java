package services.operacion.user;

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DeactivateUserRoute extends RouteBuilder {

    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {

        from("direct:deactivateUser")
            .doTry()
                .process(exchange -> {
                    String idParam = exchange.getIn().getHeader("id", String.class);
                    if (idParam == null || idParam.trim().isEmpty()) {
                        throw new IllegalArgumentException("El parámetro 'id' es obligatorio");
                    }

                    Long id = 0L;
                    try {
                        id = Long.valueOf(idParam);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("El parámetro 'id' debe ser un número válido");
                    }

                    userService.deactivateUser(id); 
                    List<UserResponse> users = new ArrayList<>(); 
                    users.add(userService.getUserById(id));

                    exchange.getMessage().setBody(users);
                    exchange.getMessage().setHeader("message", "Usuario desactivado exitosamente");
                })
                .to("direct:success") 
            .doCatch(Exception.class) 
                .to("direct:error");

    }

}
