package com.tps.orm.entity;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*; 
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

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

    @Column(name = "client_id", unique = true, nullable = false, length = 255, updatable = false)
    private String clientId;

    @Column(name = "secret", nullable = false, updatable= false, length = 512)
    private String secret;

    @Column(name = "created_by", nullable = false)
    private String createdBy;
  
    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "minutes_active", nullable = false)
    private Integer minutesActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_days", nullable = false)
    private Integer expiresDays;

    @Column(name = "expires_at", nullable = true)
    private LocalDateTime expiresAt;

    @Column(name = "refreshable", nullable = false)
    private Boolean refreshable;
  
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        clientId = generateClientId();
        secret = generateSecret();
        updatedAt = LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
 
    public LocalDateTime calculateExpiresAt() {
        return LocalDateTime.now().plusDays(expiresDays);
    }

    private String generateClientId() {
        // Genera un UUID como client ID único
        return java.util.UUID.randomUUID().toString();
    }

    private String generateSecret() {

        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);

        // Codificar en Base64 para poder guardarlo o usarlo luego 
        return Base64.getEncoder().encodeToString(key);
    }


    public void setExpiresDays(Integer expiresDays) {
        this.expiresDays = expiresDays;

        // -1 es que no expira
        if (this.expiresDays != null && this.expiresDays > 0) {
            this.expiresAt = calculateExpiresAt();
        }

    }

}
