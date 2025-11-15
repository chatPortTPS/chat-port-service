package services.operacion.user;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetUserAreas extends RouteBuilder {

    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {
       
        from("direct:getUserAreas")
            .doTry()
                .log("Getting user areas by email")
                .process((Exchange exchange) -> {
                    
                    String email = exchange.getIn().getHeader("email", String.class);

                    if (email == null || email.trim().isEmpty()) {
                        throw new IllegalArgumentException("Email es requerido");
                    }
 
                    List<String> areas = userService.getUserAreasByEmail(email.toLowerCase());

                    exchange.getIn().setBody(areas);
                })
                .to("direct:success")
            .doCatch(Exception.class) 
                .to("direct:error");
    }

}
