package services.operacion.user;

import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse implements Serializable  {

    private String username;
    private String email;
    private Boolean status;
    private LocalDateTime createdAt;

    public UserResponse(String username, String email) {
        this.username = username;
        this.email = email;
        this.status = false;
        this.createdAt = LocalDateTime.now();
    }

}
