package com.tps.orm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "area_agentes", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_area_agent", columnNames = {"area_id", "agent_id"})
       },
       indexes = {
           @Index(name = "idx_area_id", columnList = "area_id"),
           @Index(name = "idx_agent_id", columnList = "agent_id")
       })
@Data
@EqualsAndHashCode(callSuper = false)
public class AreaAgente extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Area ID cannot be null")
    @Column(name = "area_id", nullable = false)
    private Long areaId;

    @NotNull(message = "Agent cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_area_agente_agent"))
    private Agent agent;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
 
    // Business methods using PanacheEntityBase

    /**
     * Find all active area-agent relationships
     */
    public static List<AreaAgente> findAllActive() {
        return find("status", true).list();
    }

    /**
     * Find all area-agent relationships by area ID
     */
    public static List<AreaAgente> findByAreaId(Long areaId) {
        return find("areaId", areaId).list();
    }

    /**
     * Find all active area-agent relationships by area ID
     */
    public static List<AreaAgente> findActiveByAreaId(Long areaId) {
        return find("areaId = ?1 AND status = ?2", areaId, true).list();
    }

    /**
     * Find all area-agent relationships by agent
     */
    public static List<AreaAgente> findByAgent(Agent agent) {
        return find("agent", agent).list();
    }

    /**
     * Find all active area-agent relationships by agent
     */
    public static List<AreaAgente> findActiveByAgent(Agent agent) {
        return find("agent = ?1 AND status = ?2", agent, true).list();
    }

    /**
     * Find all area-agent relationships by agent ID
     */
    public static List<AreaAgente> findByAgentId(Long agentId) {
        return find("agent.id", agentId).list();
    }

    /**
     * Find all active area-agent relationships by agent ID
     */
    public static List<AreaAgente> findActiveByAgentId(Long agentId) {
        return find("agent.id = ?1 AND status = ?2", agentId, true).list();
    }

    /**
     * Find specific area-agent relationship
     */
    public static AreaAgente findByAreaIdAndAgent(Long areaId, Agent agent) {
        return find("areaId = ?1 AND agent = ?2", areaId, agent).firstResult();
    }

    /**
     * Find specific area-agent relationship by IDs
     */
    public static AreaAgente findByAreaIdAndAgentId(Long areaId, Long agentId) {
        return find("areaId = ?1 AND agent.id = ?2", areaId, agentId).firstResult();
    }

    /**
     * Check if area-agent relationship exists
     */
    public static boolean existsByAreaIdAndAgent(Long areaId, Agent agent) {
        return count("areaId = ?1 AND agent = ?2", areaId, agent) > 0;
    }

    /**
     * Check if active area-agent relationship exists
     */
    public static boolean existsActiveByAreaIdAndAgent(Long areaId, Agent agent) {
        return count("areaId = ?1 AND agent = ?2 AND status = ?3", areaId, agent, true) > 0;
    }

    /**
     * Get all agents for a specific area
     */
    public static List<Agent> getAgentsByAreaId(Long areaId) {
        return getEntityManager()
                .createQuery("SELECT aa.agent FROM AreaAgente aa WHERE aa.areaId = :areaId AND aa.status = :status", Agent.class)
                .setParameter("areaId", areaId)
                .setParameter("status", true)
                .getResultList();
    }

    /**
     * Get all area IDs for a specific agent
     */
    public static List<Long> getAreaIdsByAgent(Agent agent) {
        return getEntityManager()
                .createQuery("SELECT aa.areaId FROM AreaAgente aa WHERE aa.agent = :agent AND aa.status = :status", Long.class)
                .setParameter("agent", agent)
                .setParameter("status", true)
                .getResultList();
    }

    /**
     * Count active agents for an area
     */
    public static long countActiveAgentsByAreaId(Long areaId) {
        return count("areaId = ?1 AND status = ?2", areaId, true);
    }

    /**
     * Count active areas for an agent
     */
    public static long countActiveAreasByAgent(Agent agent) {
        return count("agent = ?1 AND status = ?2", agent, true);
    }
 
    /**
     * Get the agent ID for convenience
     */
    public Long getAgentId() {
        return this.agent != null ? this.agent.getId() : null;
    }

    /**
     * Create a new area-agent relationship
     */
    public static AreaAgente createRelationship(Long areaId, Agent agent) {
        // Check if relationship already exists
        AreaAgente existing = findByAreaIdAndAgent(areaId, agent);
        if (existing != null) { 
            return existing;
        }
        
        // Create new relationship
        AreaAgente areaAgente = new AreaAgente();
        areaAgente.setAreaId(areaId);
        areaAgente.setAgent(agent);
        areaAgente.persist();
        return areaAgente;
    }

    /**
     * Remove area-agent relationship (soft delete)
     */
    public static boolean removeRelationship(Long areaId, Agent agent) {
        AreaAgente areaAgente = findByAreaIdAndAgent(areaId, agent);
        if (areaAgente != null) {
            areaAgente.delete();
            return true;
        }
        return false;
    }
}
