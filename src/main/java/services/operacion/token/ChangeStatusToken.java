package services.operacion.token;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import com.tps.orm.service.TokenService;

@ApplicationScoped
public class ChangeStatusToken extends RouteBuilder {

    @Inject
    TokenService tokenService;

    @Override
    public void configure() throws Exception {
        
        from("direct:changeStatusToken")
            .log("Changing status of token with ID from header")
            .doTry()
                .process(exchange -> {
                    String tokenId = exchange.getIn().getHeader("tokenId", String.class);
                    String status = exchange.getIn().getHeader("status", String.class);

                    Long id = null;
                    try{
                        id = Long.valueOf(tokenId);
                    }catch(IllegalArgumentException e){
                        throw new Exception("El ID del token es invalido", e);
                    }

                    Boolean isActive = null;
                    try{
                        isActive = Boolean.valueOf(status);
                    }catch(IllegalArgumentException e){
                        throw new Exception("El valor de status es invalido", e);
                    }

                    tokenService.changeStatus(id, isActive);

                    exchange.getIn().setBody("Estado del token cambiado exitosamente");
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error")
            .end();
        
    }

}
