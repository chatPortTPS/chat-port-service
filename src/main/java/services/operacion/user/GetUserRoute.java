package services.operacion.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder; 
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetUserRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        from("direct:user")
            .doTry()
                .process(exchange -> {
                    // Lógica del servicio de usuario
                    List<String> lista = new ArrayList<>();
                    lista.add("Usuario 1");
                    lista.add("Usuario 2"); 
 
                    exchange.getMessage().setBody(lista);
                    exchange.setProperty("message", "Operación exitosa customizada");
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
