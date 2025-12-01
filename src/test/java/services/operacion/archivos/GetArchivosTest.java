package services.operacion.archivos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class GetArchivosTest {

    private CamelContext camelContext;
    private GetArchivos getArchivos;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        getArchivos = new GetArchivos();
        
        camelContext.addRoutes(getArchivos);
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:success")
                    .log("Success route called");
                from("direct:callElasticSearchHttp")
                    .process(exchange -> {
                        String mockResponse = "{"
                            + "\"aggregations\": {"
                            + "  \"distinct_titles\": {"
                            + "    \"buckets\": [{"
                            + "      \"latest_doc\": {"
                            + "        \"hits\": {"
                            + "          \"hits\": [{"
                            + "            \"_source\": {"
                            + "              \"archivo\": \"test-file.pdf\","
                            + "              \"nombre\": \"Test Document\","
                            + "              \"chunk_index\": 5"
                            + "            }"
                            + "          }]"
                            + "        }"
                            + "      }"
                            + "    }]"
                            + "  }"
                            + "}"
                            + "}";
                        exchange.getIn().setBody(mockResponse);
                    });
            }
        });
        
        camelContext.start();
    }

    @Test
    void testGetArchivosRoute() throws Exception {
        Exchange exchange = new DefaultExchange(camelContext);
        Exchange result = camelContext.createProducerTemplate().send("direct:getArchivos", exchange);
        
        assertNotNull(result.getIn().getBody());
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode resultNode = mapper.readTree(result.getIn().getBody(String.class));
        
        assertTrue(resultNode.isArray());
        assertTrue(resultNode.size() > 0);
        assertEquals("test-file.pdf", resultNode.get(0).get("archivo").asText());
        assertEquals(6, resultNode.get(0).get("total_fragmentos").asInt());
    }
}
