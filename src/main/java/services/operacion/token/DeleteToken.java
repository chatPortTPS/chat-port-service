package services.operacion.token;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import com.tps.orm.service.TokenService;

@ApplicationScoped
public class DeleteToken extends RouteBuilder {

    @Inject
    TokenService tokenService;

    @Override
    public void configure() throws Exception {
       
        from("direct:deleteToken")
            .log("Deleting token with ID from header")
            .doTry()
                .process(exchange -> {
                    String tokenId = exchange.getIn().getHeader("tokenId", String.class);

                    Long id = null;
                    try{
                        id = Long.valueOf(tokenId);
                    }catch(IllegalArgumentException e){
                        throw new Exception("El ID del token es invalido", e);
                    }

                    tokenService.deleteToken(id);

                    exchange.getIn().setBody("Token eliminado exitosamente");
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error")
            .end();

    }

}
