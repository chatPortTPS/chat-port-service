package com.chat.port.services.esb.archetypes.main.route;

import org.apache.camel.builder.RouteBuilder; 
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.chat.port.response.Response;


@ApplicationScoped
public class ApiRoutes extends RouteBuilder {
 
  @ConfigProperty(name = "api.title")
  String apiTitle;

  @ConfigProperty(name = "api.version")
  String apiVersion;

  @ConfigProperty(name = "openapi.enabled", defaultValue = "true")
  boolean openapiEnabled;

  @Inject
  SecurityHeadersProcessor securityHeadersProcessor;

  @Override
  public void configure() throws Exception {

    // Config REST sobre el server de Quarkus (mismo puerto/HTTPS)
    var restConfig = restConfiguration()
      .component("platform-http")
      .contextPath("/")
      .bindingMode(RestBindingMode.json)
      .dataFormatProperty("prettyPrint", "true")
      .enableCORS(true);

    if (openapiEnabled) {
      restConfig.apiContextPath("/openapi-camel")
                .apiProperty("api.title", apiTitle)
                .apiProperty("api.version", apiVersion)
                .apiProperty("schemes", "http");
    }
   
    

    from("direct:success")
        .process(securityHeadersProcessor)
        .process(exchange -> {
            Response response = new Response();
            response.ok(exchange.getMessage().getBody());
            exchange.getMessage().setBody(response);
        })
        .setHeader("Content-Type", constant("application/json"))
        .setHeader("X-Status-Code", constant(200)) 
        .log("Respuesta exitosa: ${exchangeProperty.message}");


    from("direct:error")
        .process(securityHeadersProcessor)
        .setHeader("Content-Type", constant("application/json"))
        .setHeader("X-Status-Code", constant(500))
        .log("Error en procesamiento: ${exception.message}");

  }

}
