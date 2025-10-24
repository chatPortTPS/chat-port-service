package com.tps.orm.entity;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode; 

@Entity
@Table(name = "token_agentes", 
       indexes = {
           @Index(name = "idx_token_id", columnList = "token_id"),
           @Index(name = "idx_agent_id", columnList = "agent_id")
       })
@Data
@EqualsAndHashCode(callSuper = false)
public class TokenAgente extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Token cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false, foreignKey = @ForeignKey(name = "fk_token_agente_token"))
    private Tokens token;

    @NotNull(message = "Agent cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false, foreignKey = @ForeignKey(name = "fk_token_agente_agent"))
    private Agent agent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    } 

}