package services.operacion.area;
 
import java.util.List; 
import org.apache.camel.LoggingLevel; 
import org.apache.camel.builder.RouteBuilder; 
import com.tps.orm.service.AreaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import services.operacion.area.dto.AreaResponse; 

@ApplicationScoped
public class GetAreasRoute extends RouteBuilder {
  
    @Inject
    AreaService areaService;

    @Override
    public void configure() throws Exception {
          
        from("direct:getAreas")
            .doTry() 
                .log(LoggingLevel.DEBUG, "Respuesta primera página obtenida")
                .process(exchange -> {
                    List<AreaResponse> allAreas = areaService.getAllAreas();
                    exchange.getIn().setBody(allAreas);
                })
                .to("direct:success")
            .doCatch(Exception.class)
                .to("direct:error")
            .end();
            
    }
}
