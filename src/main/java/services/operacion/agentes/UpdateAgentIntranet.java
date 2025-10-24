package services.operacion.agentes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import com.tps.orm.service.AgentService;

@ApplicationScoped
public class UpdateAgentIntranet extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {

        from("direct:updateAgentIntranet")
            .doTry()
                .process(exchange -> {
                    String agentId = exchange.getIn().getHeader("publicId", String.class);
                    String intranet = exchange.getIn().getHeader("intranet", String.class);

                    if (agentId == null || agentId.trim().isEmpty()) {
                        throw new IllegalArgumentException("El ID público del agente es obligatorio");
                    }

                    Long id = null;
                    try {
                        id = Long.valueOf(agentId);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("El ID público del agente debe ser un número válido");
                    }

                    Boolean changeIntranet = null;
                    try {
                        changeIntranet = Boolean.valueOf(intranet);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("El estado de intranet debe ser un valor booleano");
                    }

                    agentService.changeIntranetStatus(id, changeIntranet);

                    exchange.getIn().setBody("El estado de intranet del agente ha sido actualizado exitosamente.");
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error");

    }

}
