package services.operacion.user;

import lombok.Data;

@Data
public class DashboardResponse implements java.io.Serializable {

    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;

}
