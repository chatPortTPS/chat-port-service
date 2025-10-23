package com.tps.orm.entity;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "Tokens")
@Data
@EqualsAndHashCode(callSuper = false)
public class Tokens extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "client_id", unique = true, nullable = false, length = 255)
    private String clientId;

    @Column(name = "secret", nullable = false)
    private String secret;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "minutes_active", nullable = false)
    private Integer minutesActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "refreshable", nullable = false)
    private Boolean refreshable;

    @NotNull(message = "Agent cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false, foreignKey = @ForeignKey(name = "fk_token_agente"))
    private Agent agent;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }   

}
