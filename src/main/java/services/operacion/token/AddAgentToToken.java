package services.operacion.token;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import com.tps.orm.service.TokenService;

@ApplicationScoped
public class AddAgentToToken extends RouteBuilder {

    @Inject
    TokenService tokenService;

    @Override
    public void configure() throws Exception {
       
        from("direct:addAgentToToken")
            .log("Adding agent to token with provided parameters")
            .doTry()
                .process(exchange -> {
                    String tokenId = exchange.getIn().getHeader("tokenId", String.class);
                    String agenteId = exchange.getIn().getHeader("agentId", String.class);

                    Long idToken = null;
                    try{
                        idToken = Long.valueOf(tokenId);
                    }catch(IllegalArgumentException e){
                        throw new Exception("El ID del token es invalido", e);
                    }

                    Long idAgente = null;
                    try{
                        idAgente = Long.valueOf(agenteId);
                    }catch(IllegalArgumentException e){
                        throw new Exception("El ID del agente es invalido", e);
                    }

                    Boolean result = tokenService.vincularAgente(idToken, idAgente);

                    exchange.getIn().setBody("Agente vinculado al token exitosamente: " + result);
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error")
            .end();
        
    }

}
