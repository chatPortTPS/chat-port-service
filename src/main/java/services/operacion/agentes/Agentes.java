package services.operacion.agentes;


import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.AgentService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import services.operacion.agentes.dto.AgentRequest;


@ApplicationScoped
public class Agentes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        rest("/agentes")
            .description("Servicio para gestionar los agentes conversacionales")
            .consumes("application/json")
            .produces("application/json")

            // GET /agentes
            .get()
                .to("direct:getAgentes")

            // POST /agentes
            .post()
                .type(AgentRequest.class)
                .to("direct:createAgent");
      
    }

}
