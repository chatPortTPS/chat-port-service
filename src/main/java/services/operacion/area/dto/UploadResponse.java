package services.operacion.area.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResponse {

    @JsonProperty("uuid") 
    private String uuid;
    
    @JsonProperty("nombre") 
    private String nombre;

    @JsonProperty("correo")
    private String correo;

    @JsonProperty("autor")
    private String autor;

    @JsonProperty("agente_id")
    private String agenteId;

    @JsonProperty("nombre_agente")
    private String nombreAgente;
 
}
