package services.operacion.agentes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

class UpdateAgentTest {

    @Mock
    AgentService agentService;

    private CamelContext camelContext;
    private UpdateAgent updateAgent;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        updateAgent = new UpdateAgent();
        updateAgent.agentService = agentService;
        
        camelContext.addRoutes(updateAgent);
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
    void testUpdateAgentRoute_Success() throws Exception {
        AgentResponse mockResponse = new AgentResponse();
        mockResponse.setName("Updated Agent");
        
        when(agentService.updateAgent(any(Long.class), any(), any(), any(), any(), any(), any()))
            .thenReturn(mockResponse);
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("agentId", 1L);
        exchange.getIn().setHeader("name", "Updated Agent");
        exchange.getIn().setHeader("description", "Updated Description");
        
        camelContext.createProducerTemplate().send("direct:updateAgent", exchange);
        
        verify(agentService, times(1)).updateAgent(any(Long.class), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testUpdateAgentRoute_MissingAgentId() throws Exception {
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("name", "Updated Agent");
        
        camelContext.createProducerTemplate().send("direct:updateAgent", exchange);
        
        verify(agentService, never()).updateAgent(any(Long.class), any(), any(), any(), any(), any(), any());
    }
}
