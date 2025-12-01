package services.operacion.area.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import java.text.Normalizer;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaResponse {
    
    private Long id;
    
    private String name;

    @JsonProperty("public_id") 
    private String publicId;
    
    public AreaResponse() {}
    
    public AreaResponse(Long id, String name) {
        this.id = id;
        this.name = name;
        this.publicId = generatePublicId(name);
    }
    
    public void setNombre(String name) {
        this.name = name;
        this.publicId = generatePublicId(name);
    }

    private String generatePublicId(String name) {
        if (name == null) {
            return null;
        }
        String normalized = removeAccents(name.trim().toLowerCase());
        normalized = normalized.replaceAll("[^a-z0-9\\s]", ""); // Elimina caracteres especiales
        return normalized.replaceAll("\\s+", "_");
    }
    
    private String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        return Normalizer.normalize(text, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", ""); // Elimina marcas diacríticas (tildes, acentos)
    }
}
