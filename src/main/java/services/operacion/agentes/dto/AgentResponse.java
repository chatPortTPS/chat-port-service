package services.operacion.agentes.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tps.orm.entity.Agent;
import com.tps.orm.entity.AgentPosition;
import com.tps.orm.entity.AgentStatus;
import com.tps.orm.entity.AgentTheme;
import com.tps.orm.entity.AgentType;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AgentResponse implements java.io.Serializable {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("public_id")
    private String publicId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("status")
    private AgentStatus status;

    @JsonProperty("intranet")
    private Boolean intranet;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("user_create")
    private String userCreate;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("theme")
    private AgentTheme theme;

    @JsonProperty("position")
    private AgentPosition position;

    @JsonProperty("website")
    private String website;

    @JsonProperty("type")
    private AgentType type;

    private List<String> areas;

    private List<String> documentos;

    public AgentResponse(String name, String description, String prompt, AgentStatus status, String userCreate) {
        this.name = name;
        this.description = description;
        this.prompt = prompt;
        this.status = status;
        this.userCreate = userCreate;
        this.createdAt = LocalDateTime.now().toString();
    }

    public static AgentResponse fromEntity(Agent agent, List<String> vinculos) {
        AgentResponse response = fromEntity(agent);

        response.setAreas(new java.util.ArrayList<>());
        response.setDocumentos(new java.util.ArrayList<>());

        if (agent.getType().equals(AgentType.AREAS)){
            response.setAreas(vinculos);
        } 
        
        if (agent.getType().equals(AgentType.DOCUMENTS)){
            response.setDocumentos(vinculos);
        }
 
        return response;
    }
 
    
    public static AgentResponse fromEntity(Agent agent) {
        AgentResponse response = new AgentResponse();

        response.setId(agent.getId());
        response.setPublicId(agent.getPublicId());
        response.setName(agent.getName());
        response.setDescription(agent.getDescription());
        response.setPrompt(agent.getPrompt());
        response.setStatus(agent.getStatus());
        response.setCreatedAt(agent.getCreatedAt().toString());
        response.setUserCreate(agent.getUserCreate());
        response.setUpdatedAt(agent.getUpdatedAt().toString());
        response.setTheme(agent.getTheme());
        response.setPosition(agent.getPosition());
        response.setWebsite(agent.getWebsite());
        response.setType(agent.getType());
        response.setIntranet(agent.getIntranet());
        response.setAreas(new java.util.ArrayList<>());
        response.setDocumentos(new java.util.ArrayList<>());

        return response;
    }
}
