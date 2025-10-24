package services.operacion.token;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import com.tps.orm.service.TokenService;
import services.operacion.token.dto.TokenResponse;
import java.util.List;

@ApplicationScoped
public class GetTokensById extends RouteBuilder {

    @Inject
    TokenService tokenService;

    @Override
    public void configure() throws Exception {
       
        from("direct:getTokensById")
            .log("Getting token by ID with provided parameters")
            .doTry()
                .process(exchange -> {
                    String tokenId = exchange.getIn().getHeader("tokenId", String.class);

                    Long idToken = null;
                    try{
                        idToken = Long.valueOf(tokenId);
                    }catch(IllegalArgumentException e){
                        throw new Exception("El ID del token es invalido", e);
                    }

                    TokenResponse token = tokenService.getTokenById(idToken);

                    exchange.getIn().setBody(List.of(token)); 
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error")
            .end();
        
    }

}
