package services.operacion.token.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tps.orm.entity.TokenAgente;
import com.tps.orm.entity.Tokens;

import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

    @JsonProperty("id") 
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("secret")
    private String secret; 

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("status") 
    private String status;

    @JsonProperty("minutes_active")
    private Integer minutesActive;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("expires_at")
    private String expiresAt;

    @JsonProperty("refreshable")
    private Boolean refreshable;

    private List<String> agentes;
     

    public static TokenResponse fromEntity(com.tps.orm.entity.Tokens tokenEntity) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setId(tokenEntity.getId());
        tokenResponse.setName(tokenEntity.getName());
        tokenResponse.setClientId(tokenEntity.getClientId());
        tokenResponse.setSecret(tokenEntity.getSecret());
        tokenResponse.setCreatedBy(tokenEntity.getCreatedBy());
        tokenResponse.setStatus(tokenEntity.getStatus().toString());
        tokenResponse.setMinutesActive(tokenEntity.getMinutesActive());
        tokenResponse.setCreatedAt(tokenEntity.getCreatedAt().toString());
        tokenResponse.setExpiresAt(tokenEntity.getExpiresAt().toString());
        tokenResponse.setRefreshable(tokenEntity.getRefreshable());
        tokenResponse.setAgentes(List.of()); // Placeholder for agentes, implement as needed
        return tokenResponse;
    }


    public static TokenResponse fromEntity(Tokens tokenEntity, List<TokenAgente> tokenAgentes) {
        
        TokenResponse tokenResponse = fromEntity(tokenEntity);

        tokenResponse.setSecret("");
        
        List<String> agentes = tokenAgentes.stream()
                .map(ta -> ta.getAgent().getName())
                .toList();

        tokenResponse.setAgentes(agentes);
        return tokenResponse;
        
    }

}
