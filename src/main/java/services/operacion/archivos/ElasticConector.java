package services.operacion.archivos;

import java.util.Base64;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ElasticConector extends RouteBuilder {

    @ConfigProperty(name = "camel.component.elasticsearch.host-addresses", defaultValue = "")
    String hostElastic;


    @ConfigProperty(name = "camel.component.elasticsearch.user", defaultValue = "")
    String userElastic;


    @ConfigProperty(name = "camel.component.elasticsearch.password", defaultValue = "")
    String passwordElastic;

    @ConfigProperty(name = "elastic.index.name", defaultValue = "") 
    String elasticIndexName;

    @Override
    public void configure() throws Exception {
        
        
        from("direct:callElasticSearch") 
            .log(LoggingLevel.ERROR, "Consulta de ElasticSearch: ${body}")
            .to("elasticsearch://default?operation=Search&indexName=tps-gestor-documental")
            .log(LoggingLevel.ERROR, "Respuesta de ElasticSearch: ${body}")
        .end();

        String auth = "Basic " + Base64.getEncoder()
            .encodeToString((userElastic + ":" + passwordElastic).getBytes(java.nio.charset.StandardCharsets.UTF_8));

        from("direct:callElasticSearchHttp")  // tu consumer
            // Limpia cualquier arrastre del request entrante
            .removeHeaders("CamelHttp*")
            .removeHeader(org.apache.camel.Exchange.HTTP_PATH)
            .removeHeader(org.apache.camel.Exchange.HTTP_URI)
            .removeHeader(org.apache.camel.Exchange.HTTP_QUERY)

            // Configura la llamada a ES
            .setHeader("Authorization", constant(auth))
            .setHeader(org.apache.camel.Exchange.HTTP_METHOD, constant("POST"))
            .setHeader(org.apache.camel.Exchange.CONTENT_TYPE, constant("application/json"))

            // Fuerza el path exacto hacia Elasticsearch
            .setHeader(org.apache.camel.Exchange.HTTP_PATH, constant("/" + elasticIndexName + "/_search"))

            .log(LoggingLevel.ERROR, "Consulta a ElasticSearch: ${body}")
            // Usa endpoint sin path; bridgeEndpoint para no reenviar Host, etc.
            .to(hostElastic + "?bridgeEndpoint=true")

            .log(org.apache.camel.LoggingLevel.ERROR, "Respuesta de ElasticSearch: ${body}");




    }

}
