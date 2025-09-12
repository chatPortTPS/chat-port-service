package services.operacion.area;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class AreaRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
       
        rest("/area")
            .description("Servicio que obtiene todas las áreas de la organización desde BUK API")
            .consumes("application/json")
            .produces("application/json")
        
            // GET /area
            .get()
                .to("direct:getAreas");
    }

}
