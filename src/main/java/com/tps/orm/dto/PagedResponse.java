package com.tps.orm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    
    @JsonProperty("content")
    private List<T> content;
    
    @JsonProperty("page")
    private int page;
    
    @JsonProperty("size")
    private int size;
    
    @JsonProperty("total_elements")
    private long totalElements;
    
    @JsonProperty("total_pages")
    private int totalPages;
    
    @JsonProperty("first")
    private boolean first;
    
    @JsonProperty("last")
    private boolean last;
    
    @JsonProperty("number_of_elements")
    private int numberOfElements;
    
    @JsonProperty("empty")
    private boolean empty;

    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages((int) Math.ceil((double) totalElements / size));
        response.setFirst(page == 0);
        response.setLast(page >= response.getTotalPages() - 1);
        response.setNumberOfElements(content.size());
        response.setEmpty(content.isEmpty());
        return response;
    }
}
