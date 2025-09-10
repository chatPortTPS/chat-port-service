package com.tps.orm.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    
    private int page = 0;           // Página actual (base 0)
    private int size = 10;          // Tamaño de página
    private String sortBy;          // Campo para ordenar
    private String sortDirection = "ASC";   // Dirección del ordenamiento
    
    public PageRequest(int page, int size) {
        this.page = Math.max(0, page);
        this.size = Math.max(1, Math.min(100, size)); // Limitar entre 1 y 100
    }
    
    public int getOffset() {
        return page * size;
    }
    
    public boolean isValidSort() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }
    
    public boolean isAscending() {
        return "ASC".equalsIgnoreCase(sortDirection);
    }
}
