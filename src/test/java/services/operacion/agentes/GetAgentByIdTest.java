package services.operacion.agentes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tps.orm.service.AgentService;

import services.operacion.agentes.dto.AgentResponse;

class GetAgentByIdTest {

    @Mock
    AgentService agentService;

    private CamelContext camelContext;
    private GetAgentById getAgentById;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        getAgentById = new GetAgentById();
        getAgentById.agentService = agentService;
        
        camelContext.addRoutes(getAgentById);
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:success")
                    .log("Success route called");
                from("direct:error")
                    .log("Error route called");
            }
        });
        
        camelContext.start();
    }

    @Test
    void testGetAgentByIdRoute_Success() throws Exception {
        AgentResponse mockResponse = new AgentResponse();
        mockResponse.setPublicId("test-uuid-123");
        mockResponse.setName("Test Agent");
        
        when(agentService.getAgentByPublicId(any(String.class)))
            .thenReturn(Arrays.asList(mockResponse));
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("publicId", "test-uuid-123");
        
        camelContext.createProducerTemplate().send("direct:getAgentById", exchange);
        
        verify(agentService, times(1)).getAgentByPublicId("test-uuid-123");
    }

    @Test
    void testGetAgentByIdRoute_NotFound() throws Exception {
        when(agentService.getAgentByPublicId(any(String.class)))
            .thenThrow(new IllegalArgumentException("Agente no encontrado"));
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("publicId", "non-existent-uuid");
        
        camelContext.createProducerTemplate().send("direct:getAgentById", exchange);
        
        verify(agentService, times(1)).getAgentByPublicId("non-existent-uuid");
    }
}
