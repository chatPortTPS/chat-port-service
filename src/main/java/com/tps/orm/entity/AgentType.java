package com.tps.orm.entity;

/**
 * Enumeration representing the different types of agents based on their functionality
 */
public enum AgentType {
    
    /**
     * Agent with access to public documents and area-specific documents
     */
    AREAS("Acceso a documentos públicos y de las áreas"),
    
    /**
     * Agent with access to a document database
     */
    DOCUMENTS("Acceso a una base de datos de documentos"),
    
    /**
     * Dynamic agent that adapts based on the area
     */
    DYNAMIC("Dinámico por el área");
    
    private final String displayName;
    
    AgentType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Get the display name for the agent type
     * @return the human-readable display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if the agent type is for areas access
     * @return true if type is AREAS, false otherwise
     */
    public boolean isAreasType() {
        return this == AREAS;
    }
    
    /**
     * Check if the agent type is for documents access
     * @return true if type is DOCUMENTS, false otherwise
     */
    public boolean isDocumentsType() {
        return this == DOCUMENTS;
    }
    
    /**
     * Check if the agent type is dynamic
     * @return true if type is DYNAMIC, false otherwise
     */
    public boolean isDynamicType() {
        return this == DYNAMIC;
    }
    
    /**
     * Check if the agent type has document access capabilities
     * @return true if type is AREAS or DOCUMENTS, false otherwise
     */
    public boolean hasDocumentAccess() {
        return this == AREAS || this == DOCUMENTS;
    }
    
    /**
     * Check if the agent type is area-dependent
     * @return true if type is AREAS or DYNAMIC, false otherwise
     */
    public boolean isAreaDependent() {
        return this == AREAS || this == DYNAMIC;
    }
    
    /**
     * Get the default type for new agents
     * @return the default type (DOCUMENTS)
     */
    public static AgentType getDefault() {
        return DOCUMENTS;
    }
}
