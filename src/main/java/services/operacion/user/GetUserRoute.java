package services.operacion.user;

import org.apache.camel.builder.RouteBuilder;
import com.tps.orm.service.UserService;
import com.tps.orm.dto.PageRequest;
import com.tps.orm.dto.PagedResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetUserRoute extends RouteBuilder {

    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {
        
        from("direct:getUsers")
            .doTry()
                .process(exchange -> {
                    // Obtener parámetros de query (page, size, sortBy, sortDirection, filter)
                    String pageParam = exchange.getIn().getHeader("page", "0", String.class);
                    String sizeParam = exchange.getIn().getHeader("size", "10", String.class);
                    String sortBy = exchange.getIn().getHeader("sortBy", String.class);
                    String sortDirection = exchange.getIn().getHeader("sortDirection", "ASC", String.class);
                    String filter = exchange.getIn().getHeader("filter", String.class);
                    
                    // Crear PageRequest
                    int page = Integer.parseInt(pageParam);
                    int size = Integer.parseInt(sizeParam);
                    PageRequest pageRequest = new PageRequest(page, size);
                    
                    if (sortBy != null && !sortBy.trim().isEmpty()) {
                        pageRequest.setSortBy(sortBy);
                        pageRequest.setSortDirection(sortDirection);
                    }
                    
                    // Obtener usuarios paginados
                    PagedResponse<UserResponse> pagedResponse;
                    if (filter != null && !filter.trim().isEmpty()) {
                        pagedResponse = userService.findUsersByFilterPaged(filter, pageRequest);
                    } else {
                        pagedResponse = userService.findAllUsersPaged(pageRequest);
                    }
                    
                    exchange.getMessage().setBody(pagedResponse);
                })
                .to("direct:success")
            .doCatch(NumberFormatException.class)
                .process(exchange -> {
                    // Error en parámetros numéricos
                    exchange.getMessage().setBody(java.util.Map.of(
                        "error", "Invalid page or size parameter",
                        "message", "Page and size must be valid numbers"
                    )); 
                })
                .to("direct:error")
            .doCatch(Exception.class)
                .process(exchange -> {
                    // Manejo de excepciones generales
                    Exception ex = exchange.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT, Exception.class);
                    exchange.getMessage().setBody(java.util.Map.of(
                        "error", "Internal server error",
                        "message", ex.getMessage() != null ? ex.getMessage() : "Unknown error"
                    ));
                    exchange.getMessage().setHeader("CamelHttpResponseCode", 500);
                })
                .to("direct:error")
        .end();
        
    }
}
