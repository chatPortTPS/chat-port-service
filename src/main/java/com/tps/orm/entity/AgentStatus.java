package com.tps.orm.entity;

/**
 * Enumeration representing the different statuses an Agent can have
 */
public enum AgentStatus {
    
    /**
     * Agent is in development phase
     */
    DESARROLLO("Desarrollo"),
    
    /**
     * Agent is deactivated/inactive
     */
    DESACTIVADO("Desactivado"),
    
    /**
     * Agent is published and active
     */
    PUBLICADO("Publicado");
    
    private final String displayName;
    
    AgentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get the display name for the status
     * @return the human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if the status is active (published)
     * @return true if status is PUBLICADO, false otherwise
     */
    public boolean isActive() {
        return this == PUBLICADO;
    }
    
    /**
     * Check if the status is inactive (deactivated)
     * @return true if status is DESACTIVADO, false otherwise
     */
    public boolean isInactive() {
        return this == DESACTIVADO;
    }
    
    /**
     * Check if the status is in development
     * @return true if status is DESARROLLO, false otherwise
     */
    public boolean isInDevelopment() {
        return this == DESARROLLO;
    }
}
