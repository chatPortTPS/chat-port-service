package services.operacion.agentes;

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.AgentService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VincularAreaAgent extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:vincularAreaAgent")
            .log("Vinculando área al agente con parámetros: ${headers}")
            .doTry()
                .process(exchange -> {
                    Long agentId = exchange.getIn().getHeader("agentId", Long.class);
                    String areaPublicId = exchange.getIn().getHeader("areaPublicId", String.class);

                    // Llamar al servicio para vincular el área al agente
                    agentService.vincularAreaAlAgente(agentId, areaPublicId);

                    exchange.getIn().setBody("Área vinculada correctamente al agente.");
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .log("Error al vincular área al agente: ${exception.message}")
                .to("direct:error")
            .end();
            
        
    }

}
