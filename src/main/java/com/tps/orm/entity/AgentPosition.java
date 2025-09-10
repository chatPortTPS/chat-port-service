package com.tps.orm.entity;

/**
 * Enumeration representing the different positions an Agent can be placed on the screen
 */
public enum AgentPosition {
    
    /**
     * Top left corner of the screen
     */
    TOP_LEFT("Esquina superior izquierda"),
    
    /**
     * Top right corner of the screen
     */
    TOP_RIGHT("Esquina superior derecha"),
    
    /**
     * Bottom left corner of the screen
     */
    BOTTOM_LEFT("Esquina inferior izquierda"),
    
    /**
     * Bottom right corner of the screen
     */
    BOTTOM_RIGHT("Esquina inferior derecha");
    
    private final String displayName;
    
    AgentPosition(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get the display name for the position
     * @return the human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if the position is in a corner
     * @return true if position is in any corner, false otherwise
     */
    public boolean isCorner() {
        return this == TOP_LEFT || this == TOP_RIGHT || 
               this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
    }
    
    /**
     * Check if the position is on the left side
     * @return true if position is on the left side, false otherwise
     */
    public boolean isLeftSide() {
        return this == TOP_LEFT || this == BOTTOM_LEFT;
    }
    
    /**
     * Check if the position is on the right side
     * @return true if position is on the right side, false otherwise
     */
    public boolean isRightSide() {
        return this == TOP_RIGHT || this == BOTTOM_RIGHT;
    }
    
    /**
     * Check if the position is at the top
     * @return true if position is at the top, false otherwise
     */
    public boolean isTop() {
        return this == TOP_LEFT || this == TOP_RIGHT;
    }
    
    /**
     * Check if the position is at the bottom
     * @return true if position is at the bottom, false otherwise
     */
    public boolean isBottom() {
        return this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
    }
    

    
    /**
     * Get the default position for new agents
     * @return the default position (BOTTOM_RIGHT)
     */
    public static AgentPosition getDefault() {
        return BOTTOM_RIGHT;
    }
}
