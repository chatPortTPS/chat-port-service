package services.operacion.agentes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import java.util.List;
import com.tps.orm.service.AgentService;
import services.operacion.agentes.dto.AgentResponse;

@ApplicationScoped
public class UpdateAgent extends RouteBuilder {


    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:updateAgent")
            .log("Actualizando agente con parámetros: ${headers}")
            .doTry()
                .process(exchange -> {
                    Long agentId = exchange.getIn().getHeader("agentId", Long.class);
                    String name = exchange.getIn().getHeader("name", String.class);
                    String description = exchange.getIn().getHeader("description", String.class);
                    String prompt = exchange.getIn().getHeader("prompt", String.class);
                    String theme = exchange.getIn().getHeader("theme", String.class);
                    String position = exchange.getIn().getHeader("position", String.class);
                    String website = exchange.getIn().getHeader("website", String.class);


                    if (agentId == null) {
                        throw new IllegalArgumentException("El parámetro agentId es obligatorio para actualizar un agente.");
                    }

                    AgentResponse agentResponse = agentService.updateAgent(agentId, name, description, prompt, theme, position, website);
  
                    exchange.getIn().setBody(List.of(agentResponse));

                })
                .to("direct:success")
            .doCatch(Exception.class)
                .log("Error al actualizar el agente: ${exception.message}")
                .to("direct:error")
            .end();
        
    }

}
