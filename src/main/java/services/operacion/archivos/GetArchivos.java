package services.operacion.archivos;

import org.apache.camel.builder.RouteBuilder;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetArchivos extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        from("direct:getArchivos")
            .routeId("getArchivosRoute")
            .log("Obteniendo archivos...")
            .to("velocity:/META-INF/vm/es/getAllArchivos.vm?contentCache=false")
            .to("direct:callElasticSearchHttp")
            .process(exchange -> { 
            String response = exchange.getIn().getBody(String.class);
            
            // Parse JSON response
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(response);
            
            // Extract buckets from aggregations
            com.fasterxml.jackson.databind.JsonNode buckets = rootNode.path("aggregations")
                .path("distinct_titles").path("buckets");
            
            // Create result array
            com.fasterxml.jackson.databind.node.ArrayNode resultArray = mapper.createArrayNode();
            
            for (com.fasterxml.jackson.databind.JsonNode bucket : buckets) {
                // Get the _source from latest_doc
                com.fasterxml.jackson.databind.JsonNode sourceNode = bucket.path("latest_doc")
                .path("hits").path("hits").get(0).path("_source");
                
                // Create new object with _source data and add total_fragmentos
                com.fasterxml.jackson.databind.node.ObjectNode result = sourceNode.deepCopy();

                result.put("total_fragmentos", sourceNode.path("chunk_index").asInt() + 1);
                
                resultArray.add(result);
            }
            
            exchange.getIn().setBody(resultArray);
            })  
            .to("direct:success");
            
    }

}
