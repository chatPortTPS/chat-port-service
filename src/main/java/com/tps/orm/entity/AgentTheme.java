package com.tps.orm.entity;

/**
 * Enumeration representing the different theme types an Agent can have
 */
public enum AgentTheme {
    
    /**
     * Small floating icon
     */
    MINI("Icono flotante Pequeño"),
    
    /**
     * Compact chat interface
     */
    MEDIO("Chat Compacto"),
    
    /**
     * Full chat with all features
     */
    FULL("Chat completo con todas las funciones");
    
    private final String displayName;
    
    AgentTheme(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get the display name for the theme
     * @return the human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if the theme is minimal (icon only)
     * @return true if theme is MINI, false otherwise
     */
    public boolean isMinimal() {
        return this == MINI;
    }
    
    /**
     * Check if the theme is compact
     * @return true if theme is MEDIO, false otherwise
     */
    public boolean isCompact() {
        return this == MEDIO;
    }
    
    /**
     * Check if the theme is full featured
     * @return true if theme is FULL, false otherwise
     */
    public boolean isFullFeatured() {
        return this == FULL;
    }
    
    /**
     * Get the default theme for new agents
     * @return the default theme (MEDIO)
     */
    public static AgentTheme getDefault() {
        return MEDIO;
    }
}
