package services.operacion.agentes;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import services.operacion.agentes.dto.AgentResponse;  

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.AgentService;

@ApplicationScoped
public class GetAgentById extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:getAgentById")
            .log("Obteniendo agente por ID con parámetros: ${headers}")
            .doTry()
                .process(exchange -> {
                    String publicId = exchange.getIn().getHeader("publicId", String.class);
 
                    List<AgentResponse> agent = agentService.getAgentByPublicId(publicId);
                  
                    exchange.getIn().setBody(agent);
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .log("Error al obtener el agente por ID: ${exception.message}")
                .to("direct:error")
            .end();
        
    }

}
