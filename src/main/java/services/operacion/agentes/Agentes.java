package services.operacion.agentes;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;

import jakarta.enterprise.context.ApplicationScoped;


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
                .param().name("name")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Name of the agent (max 255 characters)")
                .endParam()
                .param().name("description")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Description of the agent (max 1000 characters)")
                .endParam()
                .param().name("prompt")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Prompt for the agent (max 5000 characters)")
                .endParam()
                .param().name("status")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Agent status")
                .endParam()
                .param().name("theme")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Agent theme")
                .endParam()
                .param().name("position")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Agent position")
                .endParam()
                .param().name("website")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Website URL (max 500 characters)")
                .endParam()
                .param().name("type")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Agent type")
                .endParam()
                .param().name("userCreate")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Creator of the agent")
                .endParam()
                .to("direct:createAgent")
                
            // PUT /agentes/vincular-area
            .put("/vincular-area")
                .param().name("agentId")
                    .type(RestParamType.query)
                    .required(true)
                    .description("ID del agente a vincular")
                .endParam()
                .param().name("areaPublicId")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Public ID del área a vincular")
                .endParam()
                .to("direct:getAgentes");


            
      
    }

}
