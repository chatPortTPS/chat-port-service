package services.operacion.agentes;


import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder; 
import com.tps.orm.service.AgentService; 
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject; 
import services.operacion.agentes.dto.AgentResponse;


@ApplicationScoped
public class GetCentralAgente extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:getCentralAgente")
            .doTry()
                .log(LoggingLevel.ERROR, "Obteniendo agente central")
                .process(exchange -> { 
                    AgentResponse centralAgent = agentService.getCentralAgent(); 
                    exchange.getIn().setBody(List.of(centralAgent)); 
                })
                .log(LoggingLevel.ERROR, "Agente central obtenido con éxito ${body}")
            .to("direct:success")
            .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Error al obtener el agente central: ${exception.message}")
                .to("direct:error")
            .end();
    }

}
