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

import services.operacion.agentes.dto.AgentRequest;
import services.operacion.agentes.dto.AgentResponse;

class CreateAgentTest {

    @Mock
    AgentService agentService;

    private CamelContext camelContext;
    private CreateAgent createAgent;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        createAgent = new CreateAgent();
        createAgent.agentService = agentService;
        
        camelContext.addRoutes(createAgent);
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
    void testCreateAgentRoute_Success() throws Exception {
        AgentResponse mockResponse = new AgentResponse();
        mockResponse.setName("Test Agent");
        
        when(agentService.createAgent(any(AgentRequest.class))).thenReturn(mockResponse);
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("name", "Test Agent");
        exchange.getIn().setHeader("description", "Test Description");
        exchange.getIn().setHeader("prompt", "Test Prompt");
        exchange.getIn().setHeader("theme", "MINI");
        exchange.getIn().setHeader("position", "BOTTOM_RIGHT");
        exchange.getIn().setHeader("website", "https://test.com");
        exchange.getIn().setHeader("type", "DYNAMIC");
        exchange.getIn().setHeader("userCreate", "testuser");
        
        camelContext.createProducerTemplate().send("direct:createAgent", exchange);
        
        verify(agentService, times(1)).createAgent(any(AgentRequest.class));
    }

    @Test
    void testCreateAgentRoute_InvalidTheme() throws Exception {
        when(agentService.createAgent(any(AgentRequest.class))).thenThrow(
            new IllegalArgumentException("Valor de tema inválido")
        );
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("name", "Test Agent");
        exchange.getIn().setHeader("description", "Test Description");
        exchange.getIn().setHeader("prompt", "Test Prompt");
        exchange.getIn().setHeader("theme", "INVALID_THEME");
        exchange.getIn().setHeader("position", "BOTTOM_RIGHT");
        exchange.getIn().setHeader("website", "https://test.com");
        exchange.getIn().setHeader("type", "DYNAMIC");
        exchange.getIn().setHeader("userCreate", "testuser");
        
        camelContext.createProducerTemplate().send("direct:createAgent", exchange);
        
        // Verify that error handling was triggered
        verify(agentService, never()).createAgent(any(AgentRequest.class));
    }
}
