package services.operacion.area.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BukApiResponse {
    
    private Pagination pagination;
    
    @JsonProperty("data")
    private List<DataContent> data;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pagination {
        private String next;
        private String previous;
        private Integer count;
        
        @JsonProperty("total_pages")
        private Integer totalPages;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataContent {
        private Long id;
        private String name;
        private String address;
        private String status;
        private String city;
        
        @JsonProperty("children_area")
        private List<ChildrenArea> childrenArea;
        
        @JsonProperty("parent_area")
        private ParentArea parentArea;
        
        @JsonProperty("first_level_id")
        private Long firstLevelId;
        
        @JsonProperty("first_level_name")
        private String firstLevelName;
        
        @JsonProperty("second_level_id")
        private Long secondLevelId;
        
        @JsonProperty("second_level_name")
        private String secondLevelName;
        
        private Integer depth;
        
        @JsonProperty("cost_center")
        private String costCenter;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChildrenArea {
        private Long id;
        private String name;
        private String commune;
        private String city;
        private String address;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParentArea {
        private Long id;
        private String name;
        private String commune;
        private String city;
        private String address;
    }
}
