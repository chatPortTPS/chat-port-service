package services.operacion.user;

import org.apache.camel.builder.RouteBuilder;

import com.tps.orm.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserDashboardRoute extends RouteBuilder {

    @Inject
    UserService userService;

    @Override
    public void configure() throws Exception {
        from("direct:dashboard")
            .log("Accediendo al dashboard de usuario")
            .process(exchange -> {

                DashboardResponse response = new DashboardResponse();
                response.setTotalUsers(userService.getTotalUsers());
                response.setActiveUsers(userService.getActiveUsers());
                response.setInactiveUsers(userService.getInactiveUsers());

                exchange.getMessage().setBody(response);

            })
            .to("direct:success");
    }

}
