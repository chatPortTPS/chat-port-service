package services.operacion.agentes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import services.operacion.agentes.dto.AgentRequest;
import services.operacion.agentes.dto.AgentResponse;

import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.AgentService;

import java.util.List;
import services.operacion.agentes.dto.AgentResponse;
import services.operacion.user.UserResponse;
import org.apache.camel.LoggingLevel;

@ApplicationScoped
public class CreateAgent extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:createAgent")
            .doTry()
                .log(LoggingLevel.INFO, "Creando agente: ${body}")
                .process(exchange -> {
                    AgentRequest request = exchange.getIn().getBody(AgentRequest.class);
                   
                    AgentResponse response = agentService.createAgent(request);

                    List<AgentResponse> agents = List.of(response);

                    System.out.println("Agente creado: " + agents.toString());

                    exchange.getMessage().setBody(agents);
                })
                .setHeader("message", simple("Agente creado exitosamente"))
                .to("direct:success")
            .doCatch(IllegalArgumentException.class)
                .log(LoggingLevel.ERROR, "Error de validación: ${exception.message}")
                .to("direct:error")
            .doCatch(Exception.class)
                .log(LoggingLevel.ERROR, "Error al crear agente: ${exception.message}")
                .to("direct:error")
            .endDoTry();


    }

}
