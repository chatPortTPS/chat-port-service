package services.operacion.area;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class AreaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
       
        rest("/area")
            .description("Servicio de área")
            .consumes("application/json")
            .produces("application/json")
        
            // GET /area
            .get()
                .to("direct:getAreas");
    }

}
