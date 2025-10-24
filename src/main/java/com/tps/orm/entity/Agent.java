package com.tps.orm.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @Size(max = 1000, message = "Prompt cannot exceed 1000 characters")
    @Column(name = "prompt", length = 1000)
    private String prompt;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AgentStatus status = AgentStatus.DESARROLLO;

    @NotNull(message = "Intranet flag cannot be null")
    @Column(name = "intranet", nullable = false)
    private Boolean intranet;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotBlank(message = "User creator cannot be blank")
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
        intranet = true;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
}
