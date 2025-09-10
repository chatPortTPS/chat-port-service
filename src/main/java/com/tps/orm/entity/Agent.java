package com.tps.orm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Agents", uniqueConstraints = {
    @UniqueConstraint(name = "uk_agent_public_id", columnNames = "public_id")
})
@Data
@EqualsAndHashCode(callSuper = false)
public class Agent extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 36, message = "Public ID cannot exceed 36 characters")
    @Column(name = "public_id", unique = true, nullable = false, length = 36)
    private String publicId;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Size(max = 5000, message = "Prompt cannot exceed 5000 characters")
    @Column(name = "prompt", length = 5000, columnDefinition = "TEXT")
    private String prompt;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AgentStatus status = AgentStatus.DESARROLLO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Size(max = 255, message = "User cannot exceed 255 characters")
    @Column(name = "created_by", nullable = false, length = 255)
    private String userCreate;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @NotNull(message = "Theme cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "theme", nullable = false, length = 10)
    private AgentTheme theme = AgentTheme.getDefault();

    @NotNull(message = "Position cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, length = 15)
    private AgentPosition position = AgentPosition.getDefault();

    @Size(max = 500, message = "Website URL cannot exceed 500 characters")
    @Column(name = "website", length = 500)
    private String website;
 
    @NotNull(message = "Type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    private AgentType type = AgentType.getDefault();
 
    @PrePersist
    protected void onCreate() {
        if (publicId == null || publicId.isEmpty()) {
            publicId = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Business methods using PanacheEntityBase
    
    /**
     * Find an agent by its public ID
     */
    public static Agent findByPublicId(String publicId) {
        return find("publicId", publicId).firstResult();
    }
    
    /**
     * Find all active agents (published)
     */
    public static List<Agent> findAllActive() {
        return find("status", AgentStatus.PUBLICADO).list();
    }
    
    /**
     * Find all agents by status
     */
    public static List<Agent> findByStatus(AgentStatus status) {
        return find("status", status).list();
    }
    
    /**
     * Find agents by type
     */
    public static List<Agent> findByType(AgentType type) {
        return find("type", type).list();
    }
    
    /**
     * Find all agents with areas access
     */
    public static List<Agent> findAreasTypeAgents() {
        return find("type", AgentType.AREAS).list();
    }
    
    /**
     * Find all agents with documents access
     */
    public static List<Agent> findDocumentsTypeAgents() {
        return find("type", AgentType.DOCUMENTS).list();
    }
    
    /**
     * Find all dynamic agents
     */
    public static List<Agent> findDynamicTypeAgents() {
        return find("type", AgentType.DYNAMIC).list();
    }
    
    /**
     * Find all agents that have document access capabilities
     */
    public static List<Agent> findDocumentAccessAgents() {
        return find("type IN (?1, ?2)", AgentType.AREAS, AgentType.DOCUMENTS).list();
    }
    
    /**
     * Find all agents that are area-dependent
     */
    public static List<Agent> findAreaDependentAgents() {
        return find("type IN (?1, ?2)", AgentType.AREAS, AgentType.DYNAMIC).list();
    }
    
    /**
     * Find agents by theme
     */
    public static List<Agent> findByTheme(AgentTheme theme) {
        return find("theme", theme).list();
    }
    
    /**
     * Find all minimal theme agents
     */
    public static List<Agent> findMinimalThemeAgents() {
        return find("theme", AgentTheme.MINI).list();
    }
    
    /**
     * Find all compact theme agents
     */
    public static List<Agent> findCompactThemeAgents() {
        return find("theme", AgentTheme.MEDIO).list();
    }
    
    /**
     * Find all full-featured theme agents
     */
    public static List<Agent> findFullThemeAgents() {
        return find("theme", AgentTheme.FULL).list();
    }
    
    /**
     * Find agents by position
     */
    public static List<Agent> findByPosition(AgentPosition position) {
        return find("position", position).list();
    }
    
    /**
     * Find all agents positioned in corners
     */
    public static List<Agent> findCornerPositionAgents() {
        return find("position IN (?1, ?2, ?3, ?4)", 
                   AgentPosition.TOP_LEFT, AgentPosition.TOP_RIGHT, 
                   AgentPosition.BOTTOM_LEFT, AgentPosition.BOTTOM_RIGHT).list();
    }
    
    /**
     * Find all agents positioned on the left side
     */
    public static List<Agent> findLeftSideAgents() {
        return find("position IN (?1, ?2)", 
                   AgentPosition.TOP_LEFT, AgentPosition.BOTTOM_LEFT).list();
    }
    
    /**
     * Find all agents positioned on the right side
     */
    public static List<Agent> findRightSideAgents() {
        return find("position IN (?1, ?2)", 
                   AgentPosition.TOP_RIGHT, AgentPosition.BOTTOM_RIGHT).list();
    }
    
    /**
     * Soft delete - deactivate the agent instead of deleting
     */
    public void deactivate() {
        this.status = AgentStatus.DESACTIVADO;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Activate the agent - set status to published
     */
    public void activate() {
        this.status = AgentStatus.PUBLICADO;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent status to development
     */
    public void setToDevelopment() {
        this.status = AgentStatus.DESARROLLO;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Publish the agent
     */
    public void publish() {
        this.status = AgentStatus.PUBLICADO;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Check if agent is active (published)
     */
    public boolean isActive() {
        return this.status != null && this.status.isActive();
    }
    
    /**
     * Check if agent is in development
     */
    public boolean isInDevelopment() {
        return this.status != null && this.status.isInDevelopment();
    }
    
    /**
     * Check if agent is deactivated
     */
    public boolean isDeactivated() {
        return this.status != null && this.status.isInactive();
    }
    
    /**
     * Set agent theme to minimal (icon)
     */
    public void setMinimalTheme() {
        this.theme = AgentTheme.MINI;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent theme to compact
     */
    public void setCompactTheme() {
        this.theme = AgentTheme.MEDIO;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent theme to full-featured
     */
    public void setFullTheme() {
        this.theme = AgentTheme.FULL;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Check if agent has minimal theme
     */
    public boolean hasMinimalTheme() {
        return this.theme != null && this.theme.isMinimal();
    }
    
    /**
     * Check if agent has compact theme
     */
    public boolean hasCompactTheme() {
        return this.theme != null && this.theme.isCompact();
    }
    
    /**
     * Check if agent has full-featured theme
     */
    public boolean hasFullTheme() {
        return this.theme != null && this.theme.isFullFeatured();
    }
    
    /**
     * Set agent position to top left corner
     */
    public void setTopLeftPosition() {
        this.position = AgentPosition.TOP_LEFT;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent position to top right corner
     */
    public void setTopRightPosition() {
        this.position = AgentPosition.TOP_RIGHT;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent position to bottom left corner
     */
    public void setBottomLeftPosition() {
        this.position = AgentPosition.BOTTOM_LEFT;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent position to bottom right corner
     */
    public void setBottomRightPosition() {
        this.position = AgentPosition.BOTTOM_RIGHT;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }

    /**
     * Set agent position
     */
    public void setPosition(AgentPosition position) {
        this.position = position;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Check if agent is positioned in a corner
     */
    public boolean isInCorner() {
        return this.position != null && this.position.isCorner();
    }
    
    /**
     * Check if agent is positioned on the left side
     */
    public boolean isOnLeftSide() {
        return this.position != null && this.position.isLeftSide();
    }
    
    /**
     * Check if agent is positioned on the right side
     */
    public boolean isOnRightSide() {
        return this.position != null && this.position.isRightSide();
    }
    
    /**
     * Check if agent is positioned at the top
     */
    public boolean isAtTop() {
        return this.position != null && this.position.isTop();
    }
    
    /**
     * Check if agent is positioned at the bottom
     */
    public boolean isAtBottom() {
        return this.position != null && this.position.isBottom();
    }
    
    /**
     * Set agent type to areas access
     */
    public void setAreasType() {
        this.type = AgentType.AREAS;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent type to documents access
     */
    public void setDocumentsType() {
        this.type = AgentType.DOCUMENTS;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent type to dynamic
     */
    public void setDynamicType() {
        this.type = AgentType.DYNAMIC;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Set agent type
     */
    public void setType(AgentType type) {
        this.type = type;
        this.updatedAt = LocalDateTime.now();
        this.persist();
    }
    
    /**
     * Check if agent is of areas type
     */
    public boolean isAreasType() {
        return this.type != null && this.type.isAreasType();
    }
    
    /**
     * Check if agent is of documents type
     */
    public boolean isDocumentsType() {
        return this.type != null && this.type.isDocumentsType();
    }
    
    /**
     * Check if agent is of dynamic type
     */
    public boolean isDynamicType() {
        return this.type != null && this.type.isDynamicType();
    }
    
    /**
     * Check if agent has document access capabilities
     */
    public boolean hasDocumentAccess() {
        return this.type != null && this.type.hasDocumentAccess();
    }
    
    /**
     * Check if agent is area-dependent
     */
    public boolean isAreaDependent() {
        return this.type != null && this.type.isAreaDependent();
    }

}
