package com.tps.orm.repository;

import java.util.Optional;

import com.tps.orm.entity.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    /**
     * Buscar usuario por username
     */
    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    /**
     * Buscar usuario por email
     */
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
 
    /**
     * Buscar usuario por username o email
     */
    public Optional<User> findByUsernameOrEmail(String username, String email) {
        return find("username = ?1 or email = ?2", username, email).firstResultOptional();
    }

    /**
     * Verificar si existe username
     */
    public boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }

    /**
     * Verificar si existe email
     */
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    /**
     * Obtener usuarios activos
     */
    public java.util.List<User> findActiveUsers() {
        return list("status", true);
    }

    /**
     * Obtener usuarios inactivos
     */
    public java.util.List<User> findInactiveUsers() {
        return list("status", false);
    }

    /**
     * Activar/desactivar usuario
     */
    public void updateUserStatus(Long userId, boolean status) {
        update("status = ?1 where id = ?2", status, userId);
    }
}
