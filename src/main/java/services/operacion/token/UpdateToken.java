package services.operacion.token;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.tps.orm.service.TokenService;
import org.apache.camel.builder.RouteBuilder;
import services.operacion.token.dto.TokenResponse;
import java.util.List;

@ApplicationScoped
public class UpdateToken extends  RouteBuilder {

    @Inject
    TokenService tokenService;

    @Override
    public void configure() throws Exception {
        
        from("direct:updateToken")
            .log("Updating token with provided parameters")
            .doTry()
                .process(exchange -> {
                    String tokenId = exchange.getIn().getHeader("tokenId", String.class);
                    String name = exchange.getIn().getHeader("name", String.class);
                    String expirationTime = exchange.getIn().getHeader("expirationTime", String.class);
                    String minutesToExpire = exchange.getIn().getHeader("minutesToExpire", String.class);
                    String refreshable = exchange.getIn().getHeader("refreshable", String.class);

                    Long id = null;
                    try{
                        id = Long.valueOf(tokenId);
                    }catch(IllegalArgumentException e){
                        throw new Exception("El ID del token es invalido", e);
                    }

                    Integer diasToExpire = null;
                    try{
                        diasToExpire = Integer.valueOf(expirationTime);
                    }catch(IllegalArgumentException e){
                        throw new Exception("Los dias a expirar son invalidos", e);
                    }

                    Integer minToExpire = null;
                    try{
                        minToExpire = Integer.valueOf(minutesToExpire);
                    }catch(IllegalArgumentException e){
                        throw new Exception("Los minutos a expirar son invalidos", e);
                    }

                    Boolean isRefreshable = false;
                    try{
                        isRefreshable = Boolean.valueOf(refreshable);
                    }catch(IllegalArgumentException e){
                        throw new Exception("El valor de refreshable es invalido", e);
                    }

                    if (name == null || name.isEmpty()){
                        throw new Exception("El nombre del token no puede ser nulo o vacio");
                    }

 
                    TokenResponse token = tokenService.update(id, name, minToExpire, diasToExpire, isRefreshable);

                    token.setSecret("");
 
                    exchange.getIn().setBody(List.of(token));
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error")
            .end();

    }

}
