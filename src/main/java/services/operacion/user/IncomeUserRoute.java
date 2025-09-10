package services.operacion.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.UserService;

@ApplicationScoped
public class IncomeUserRoute extends  RouteBuilder {

    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {
       
        from("direct:incomeUser")
            .doTry()
                .process(exchange -> {
                    String username = exchange.getIn().getHeader("username", String.class);
                    
                    if (username == null || username.trim().isEmpty()) {
                        throw new IllegalArgumentException("El parámetro 'username' es obligatorio");
                    }
                   
                    userService.incomeUser(username);
                     
                    exchange.getMessage().setBody("");
                    exchange.getMessage().setHeader("message", "Usuario ingresado exitosamente");
                })
                .to("direct:success") 
            .doCatch(Exception.class) 
                .to("direct:error");

    }


}
