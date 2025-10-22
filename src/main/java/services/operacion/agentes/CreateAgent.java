package services.operacion.agentes;

import java.util.List;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.entity.AgentPosition;
import com.tps.orm.entity.AgentStatus;
import com.tps.orm.entity.AgentTheme;
import com.tps.orm.entity.AgentType;
import com.tps.orm.service.AgentService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import services.operacion.agentes.dto.AgentRequest;
import services.operacion.agentes.dto.AgentResponse;

@ApplicationScoped
public class CreateAgent extends RouteBuilder {

    @Inject
    AgentService agentService;

    @Override
    public void configure() throws Exception {
       
        from("direct:createAgent")
            .doTry()
                .log(LoggingLevel.INFO, "Creando agente con parámetros: ${headers}")
                .process(exchange -> {
                    // Extraer parámetros query del exchange
                    String name = exchange.getIn().getHeader("name", String.class);
                    String description = exchange.getIn().getHeader("description", String.class);
                    String prompt = exchange.getIn().getHeader("prompt", String.class);
                    String status = exchange.getIn().getHeader("status", String.class);
                    String theme = exchange.getIn().getHeader("theme", String.class);
                    String position = exchange.getIn().getHeader("position", String.class);
                    String website = exchange.getIn().getHeader("website", String.class);
                    String type = exchange.getIn().getHeader("type", String.class);
                    String userCreate = exchange.getIn().getHeader("userCreate", String.class);
                    
                    // Crear el objeto AgentRequest a partir de los parámetros
                    AgentRequest request = new AgentRequest();
                    request.setName(name);
                    request.setDescription(description);
                    request.setPrompt(prompt);
                    request.setWebsite(website);
                    request.setUserCreate(userCreate);
                    
                    // Convertir strings a enums si no son null o vacíos
                    if (status != null && !status.trim().isEmpty()) {
                        try {
                            request.setStatus(AgentStatus.valueOf(status.trim().toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Valor de estado inválido: " + status + ". Valores válidos son: " + java.util.Arrays.toString(AgentStatus.values()));
                        }
                    }
                    
                    if (theme != null && !theme.trim().isEmpty()) {
                        try {
                            request.setTheme(AgentTheme.valueOf(theme.trim().toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Valor de tema inválido: " + theme + ". Valores válidos son: " + java.util.Arrays.toString(AgentTheme.values()));
                        }
                    }
                    
                    if (position != null && !position.trim().isEmpty()) {
                        try {
                            request.setPosition(AgentPosition.valueOf(position.trim().toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Valor de posición inválido: " + position + ". Valores válidos son: " + java.util.Arrays.toString(AgentPosition.values()));
                        }
                    }
                    
                    if (type != null && !type.trim().isEmpty()) {
                        try {
                            request.setType(AgentType.valueOf(type.trim().toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Valor de tipo inválido: " + type + ". Valores válidos son: " + java.util.Arrays.toString(AgentType.values()));
                        }
                    }
                   
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
