package services.operacion.agentes.dto;

import com.tps.orm.entity.AgentPosition;
import com.tps.orm.entity.AgentStatus;
import com.tps.orm.entity.AgentTheme;
import com.tps.orm.entity.AgentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AgentRequest {
    
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @Size(max = 5000, message = "Prompt cannot exceed 5000 characters")
    private String prompt;
    
    private AgentStatus status;
    
    private AgentTheme theme;
    
    private AgentPosition position;
    
    @Size(max = 500, message = "Website URL cannot exceed 500 characters")
    private String website;
    
    private AgentType type;

    @NotBlank(message = "Creator cannot be blank")
    private String userCreate;
    
}
