package services.operacion.user;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
public class UserResponse implements Serializable  {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("status")
    private Boolean status;
    
    @JsonProperty("created_at") 
    private String createdAt;

    public UserResponse(String username, String email) {
        this.username = username;
        this.email = email;
        this.status = Boolean.TRUE;
        this.createdAt = LocalDateTime.now().toString();
    }

}
