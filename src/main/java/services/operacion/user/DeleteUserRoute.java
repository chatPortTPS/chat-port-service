package services.operacion.user;

import com.tps.orm.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DeleteUserRoute extends UserRoute {
    
    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {
       
        from("direct:deleteUser")
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
  
                    boolean deleted = userService.deleteUser(id);
 
                    if (deleted) {
                       exchange.getMessage().setHeader("message", "Usuario eliminado exitosamente");
                    } else {
                        exchange.getMessage().setHeader("message", "Usuario no encontrado");
                    }

                    exchange.getMessage().setBody("");
                })
                .to("direct:success") 
            .doCatch(Exception.class) 
                .to("direct:error");

    }

}
