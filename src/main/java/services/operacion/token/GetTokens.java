package services.operacion.token;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.TokenService;
import services.operacion.token.dto.TokenResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetTokens extends RouteBuilder {

    @Inject
    TokenService tokenService;

    @Override
    public void configure() throws Exception {
        
        from("direct:getTokens")
            .routeId("getTokensRoute")
            .doTry()
                .process(exchange -> {
                   
                    List<TokenResponse> response = tokenService.getAllTokens();

                    response.forEach(token -> token.setSecret(""));

                    exchange.getIn().setBody(response);
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error")
            .end();

    }

}
