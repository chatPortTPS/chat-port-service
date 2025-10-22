package services.operacion.agentes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import services.operacion.agentes.dto.AgentResponse;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.AgentService;

@ApplicationScoped
public class GetAgentes extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:getAgentes") 
            .log("Procesando solicitud para /agentes")
            .process(exchange -> {
                List<AgentResponse> agents = agentService.getAllAgents();
                exchange.getMessage().setBody(agents);
            })
            .to("direct:success");

    }

}

      