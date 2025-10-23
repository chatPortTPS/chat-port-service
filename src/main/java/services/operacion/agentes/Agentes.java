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

            
            .get("/{publicId}")
                .param().name("publicId")
                    .type(RestParamType.path)
                    .required(true)
                    .description("Id público del agente a obtener (uuid)")
                .endParam()
                .to("direct:getAgentById")
             
            // POST /agentes
            .post()
                .param()
                    .name("name")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Name of the agent (max 255 characters)")
                .endParam()
                .param()
                    .name("description")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Description of the agent (max 1000 characters)")
                .endParam()
                .param()
                    .name("prompt")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Prompt for the agent (max 5000 characters)")
                .endParam()
                .param()
                    .name("theme")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Agent theme, valid values: " + java.util.Arrays.toString(com.tps.orm.entity.AgentTheme.values()))
                .endParam()
                .param()
                    .name("position")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Agent position, valid values: " + java.util.Arrays.toString(com.tps.orm.entity.AgentPosition.values()))
                .endParam()
                .param()
                    .name("website")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Website URL (max 500 characters)")
                .endParam()
                .param()
                    .name("type")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Agent type, valid values: " + java.util.Arrays.toString(com.tps.orm.entity.AgentType.values()))
                .endParam()
                .param()
                    .name("userCreate")
                    .type(RestParamType.query)
                    .required(true)
                    .description("Creator of the agent")
                .endParam()
                .to("direct:createAgent")


            // PUT /agentes/area
            .put("/area")
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
                .to("direct:vincularAreaAgent")

            
            .delete()
                .param().name("agentId")
                    .type(RestParamType.query)
                    .required(true)
                    .description("ID del agente a eliminar")
                .endParam()
                .to("direct:deleteAgent")

            .put()
                .param()
                    .name("agentId")
                    .type(RestParamType.query)
                    .required(true)
                    .description("ID del agente a actualizar")
                .endParam()
                .param()
                    .name("name")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Name of the agent (max 255 characters)")
                .endParam()
                .param()
                    .name("description")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Description of the agent (max 1000 characters)")
                .endParam()
                .param()
                    .name("prompt")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Prompt for the agent (max 5000 characters)")
                .endParam() 
                .param()
                    .name("theme")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Agent theme, valid values: " + java.util.Arrays.toString(com.tps.orm.entity.AgentTheme.values()))
                .endParam()
                .param()
                    .name("position")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Agent position, valid values: " + java.util.Arrays.toString(com.tps.orm.entity.AgentPosition.values()))
                .endParam()
                .param()
                    .name("website")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Website URL (max 500 characters)")
                .endParam()
                .to("direct:updateAgent")


            .put("/desactivar")
                .param().name("agentId")
                    .type(RestParamType.query)
                    .required(true)
                    .description("ID del agente a desactivar")
                .endParam()
                .to("direct:deactivateAgent")

            .put("/publicar")
                .param().name("agentId")
                    .type(RestParamType.query)
                    .required(true)
                    .description("ID del agente a publicar")
                .endParam()
                .to("direct:publishAgent")


            .post("/documento")
                .param().name("agentId")
                    .type(RestParamType.query)
                    .required(true)
                    .description("ID del agente al que se le añade el documento")
                .endParam()
                .param().name("documentContent")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Contenido del documento en Base64")
                .endParam()
                .param().name("fileName")
                    .type(RestParamType.query)
                    .required(false)
                    .description("Nombre del archivo del documento")
                .endParam()
                .to("direct:addDocumentToAgent");
            
            

            
      
    }

}

