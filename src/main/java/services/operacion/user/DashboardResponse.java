package services.operacion.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DashboardResponse implements java.io.Serializable {

    @JsonProperty("total_users")
    private long totalUsers;
    
    @JsonProperty("active_users")
    private long activeUsers;

    @JsonProperty("inactive_users")
    private long inactiveUsers;

}
