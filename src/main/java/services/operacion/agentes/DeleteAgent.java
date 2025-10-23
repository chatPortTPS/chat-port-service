package services.operacion.agentes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.tps.orm.service.AgentService;

import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class DeleteAgent extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:deleteAgent")
            .log("Eliminando agente con parámetros: ${headers}")
            .doTry()
                .process(exchange -> {
                    Long agentId = exchange.getIn().getHeader("agentId", Long.class);
 
                    if (agentId == null) {
                        throw new IllegalArgumentException("El ID del agente es obligatorio para eliminarlo.");
                    }
                    
                    agentService.deleteAgentById(agentId);
                    exchange.getIn().setBody("Agente eliminado correctamente con ID: " + agentId);
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .log("Error al eliminar el agente: ${exception.message}")
                .to("direct:error")
            .end();
    }

}
