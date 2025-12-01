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

class DeleteAgentTest {

    @Mock
    AgentService agentService;

    private CamelContext camelContext;
    private DeleteAgent deleteAgent;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        deleteAgent = new DeleteAgent();
        deleteAgent.agentService = agentService;
        
        camelContext.addRoutes(deleteAgent);
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
    void testDeleteAgentRoute_Success() throws Exception {
        when(agentService.deleteAgentById(any(Long.class))).thenReturn(true);
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("agentId", 1L);
        
        camelContext.createProducerTemplate().send("direct:deleteAgent", exchange);
        
        verify(agentService, times(1)).deleteAgentById(1L);
    }

    @Test
    void testDeleteAgentRoute_MissingAgentId() throws Exception {
        Exchange exchange = new DefaultExchange(camelContext);
        
        camelContext.createProducerTemplate().send("direct:deleteAgent", exchange);
        
        verify(agentService, never()).deleteAgentById(any(Long.class));
    }

    @Test
    void testDeleteAgentRoute_ServiceThrowsException() throws Exception {
        when(agentService.deleteAgentById(any(Long.class)))
            .thenThrow(new IllegalArgumentException("Agente no encontrado"));
        
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("agentId", 999L);
        
        camelContext.createProducerTemplate().send("direct:deleteAgent", exchange);
        
        verify(agentService, times(1)).deleteAgentById(999L);
    }
}
