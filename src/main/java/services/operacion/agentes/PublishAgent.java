package services.operacion.agentes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.tps.orm.service.AgentService;
import org.apache.camel.builder.RouteBuilder;
import services.operacion.agentes.dto.AgentResponse;
import java.util.List;

@ApplicationScoped
public class PublishAgent extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:publishAgent")
            .log("Publicando agente con parámetros: ${headers}")
            .doTry()
                .process(exchange -> {
                    Long agentId = exchange.getIn().getHeader("agentId", Long.class);

                    if (agentId == null) {
                        throw new IllegalArgumentException("El identificador del agente es obligatorio para publicar un agente.");
                    }

                    AgentResponse response = agentService.publishAgent(agentId);

                    exchange.getIn().setBody(List.of(response));

                })
                .to("direct:success")
            .doCatch(Exception.class)
                .log("Error al publicar el agente: ${exception.message}")
                .to("direct:error")
            .end();
        
    }

}
