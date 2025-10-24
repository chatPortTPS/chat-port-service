package services.operacion.token;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.tps.orm.service.TokenService;
import org.apache.camel.builder.RouteBuilder;
import services.operacion.token.dto.TokenResponse;
import java.util.List;

@ApplicationScoped
public class CreateToken extends RouteBuilder {

    @Inject
    TokenService tokenService;
   
    @Override
    public void configure() throws Exception {

        from("direct:createToken")
            .log("Creating a new token with provided parameters")
            .doTry()
                .process(exchange -> {
                    String name = exchange.getIn().getHeader("name", String.class);
                    String expirationTime = exchange.getIn().getHeader("expirationTime", String.class);
                    String minutesToExpire = exchange.getIn().getHeader("minutesToExpire", String.class);
                    String refreshable = exchange.getIn().getHeader("refreshable", String.class);
                    String createdBy = exchange.getIn().getHeader("createdBy", String.class);

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

                    if (createdBy == null || createdBy.isEmpty()){
                        throw new Exception("El campo createdBy no puede ser nulo o vacio");
                    }
 
                    TokenResponse token = tokenService.create(name, minToExpire, diasToExpire, isRefreshable, createdBy);
 
                    exchange.getIn().setBody(List.of(token));

                })
                .to("direct:success")
            .doCatch(Exception.class)
                .log("Error occurred while creating token: ${exception.message}")
                .to("direct:error");

    }

}
