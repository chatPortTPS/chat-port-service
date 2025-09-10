package com.tps.orm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;
  
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "accessed_at")
    private LocalDateTime accessedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        accessedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        accessedAt = LocalDateTime.now();
    }

}
