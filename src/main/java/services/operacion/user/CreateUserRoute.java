package services.operacion.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder; 
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateUserRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        

        from("direct:creteUser")
            .doTry()
                .process(exchange -> {
                    // Lógica para crear un usuario
                    String rutCliente = exchange.getIn().getBody(String.class);
                    List<String> lista = new ArrayList<>();
                    lista.add(rutCliente); 
                    
                    exchange.getMessage().setBody(lista);
                    exchange.setProperty("message", "Usuario creado exitosamente");
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .process(exchange -> {
                    // Manejo de excepciones
                    List<String> lista = new ArrayList<>();
                    exchange.getMessage().setBody(lista);
                })
                .to("direct:error")
            .endDoTry()
        .end();

        
    }
}
