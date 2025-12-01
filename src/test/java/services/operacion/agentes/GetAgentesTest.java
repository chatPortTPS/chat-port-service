package services.operacion.agentes;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

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

class GetAgentesTest {

    @Mock
    AgentService agentService;

    private CamelContext camelContext;
    private GetAgentes getAgentes;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        getAgentes = new GetAgentes();
        getAgentes.agentService = agentService;
        
        camelContext.addRoutes(getAgentes);
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:success")
                    .log("Success route called");
            }
        });
        
        camelContext.start();
    }

    @Test
    void testGetAgentesRoute() throws Exception {
        AgentResponse response1 = new AgentResponse();
        response1.setName("Agent 1");
        
        AgentResponse response2 = new AgentResponse();
        response2.setName("Agent 2");
        
        List<AgentResponse> agents = Arrays.asList(response1, response2);
        
        when(agentService.getAllAgents()).thenReturn(agents);
        
        Exchange exchange = new DefaultExchange(camelContext);
        Exchange result = camelContext.createProducerTemplate().send("direct:getAgentes", exchange);
        
        verify(agentService, times(1)).getAllAgents();
    }
}
