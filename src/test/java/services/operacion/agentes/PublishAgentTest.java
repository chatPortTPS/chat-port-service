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

import com.tps.orm.entity.AgentStatus;
import com.tps.orm.service.AgentService;

import services.operacion.agentes.dto.AgentResponse;

class PublishAgentTest {

    @Mock
    AgentService agentService;

    private CamelContext camelContext;
    private PublishAgent publishAgent;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        publishAgent = new PublishAgent();
        publishAgent.agentService = agentService;
        
        camelContext.addRoutes(publishAgent);
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
    void testPublishAgentRoute_Success() throws Exception {
        AgentResponse mockResponse = new AgentResponse();
        mockResponse.setName("Published Agent");
        mockResponse.setStatus(AgentStatus.PUBLICADO);
        
        when(agentService.publishAgent(any(Long.class))).thenReturn(mockResponse);
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("agentId", 1L);
        
        camelContext.createProducerTemplate().send("direct:publishAgent", exchange);
        
        verify(agentService, times(1)).publishAgent(1L);
    }

    @Test
    void testPublishAgentRoute_MissingAgentId() throws Exception {
        Exchange exchange = new DefaultExchange(camelContext);
        
        camelContext.createProducerTemplate().send("direct:publishAgent", exchange);
        
        verify(agentService, never()).publishAgent(any(Long.class));
    }

    @Test
    void testPublishAgentRoute_ServiceThrowsException() throws Exception {
        when(agentService.publishAgent(any(Long.class)))
            .thenThrow(new IllegalArgumentException("Agente no encontrado"));
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("agentId", 999L);
        
        camelContext.createProducerTemplate().send("direct:publishAgent", exchange);
        
        verify(agentService, times(1)).publishAgent(999L);
    }
}
