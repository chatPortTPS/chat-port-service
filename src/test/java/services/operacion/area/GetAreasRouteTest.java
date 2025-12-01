package services.operacion.area;

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

import com.tps.orm.service.AreaService;

import services.operacion.area.dto.AreaResponse;

class GetAreasRouteTest {

    @Mock
    AreaService areaService;

    private CamelContext camelContext;
    private GetAreasRoute getAreasRoute;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        camelContext = new DefaultCamelContext();
        getAreasRoute = new GetAreasRoute();
        getAreasRoute.areaService = areaService;
        
        camelContext.addRoutes(getAreasRoute);
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
    void testGetAreasRoute_Success() throws Exception {
        AreaResponse area1 = new AreaResponse();
        area1.setNombre("area_1");
        
        AreaResponse area2 = new AreaResponse();
        area2.setNombre("area_2");
        
        List<AreaResponse> areas = Arrays.asList(area1, area2);
        
        when(areaService.getAllAreas()).thenReturn(areas);
        
        Exchange exchange = new DefaultExchange(camelContext);
        Exchange result = camelContext.createProducerTemplate().send("direct:getAreas", exchange);
        
        verify(areaService, times(1)).getAllAreas();
    }

    @Test
    void testGetAreasRoute_Exception() throws Exception {
        when(areaService.getAllAreas()).thenThrow(new RuntimeException("Test exception"));
        
        Exchange exchange = new DefaultExchange(camelContext);
        camelContext.createProducerTemplate().send("direct:getAreas", exchange);
        
        verify(areaService, times(1)).getAllAreas();
    }
}
