package com.tps.orm.repository;

import java.util.List;
import java.util.Optional;

import com.tps.orm.dto.PageRequest;
import com.tps.orm.entity.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
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
 
 
  
    
    /**
     * Obtener todos los usuarios con paginado
     */
    public List<User> findAllPaged(PageRequest pageRequest) {
        Page page = Page.of(pageRequest.getPage(), pageRequest.getSize());
        
        if (pageRequest.isValidSort()) {
            Sort sort = pageRequest.isAscending() 
                ? Sort.by(pageRequest.getSortBy()).ascending()
                : Sort.by(pageRequest.getSortBy()).descending();
            return findAll(sort).page(page).list();
        }
        
        return findAll().page(page).list();
    }

    /**
     * Obtener usuarios activos con paginado
     */ 
    public List<User> findActiveUsersPaged(PageRequest pageRequest) {
        Page page = Page.of(pageRequest.getPage(), pageRequest.getSize());
        
        if (pageRequest.isValidSort()) {
            Sort sort = pageRequest.isAscending() 
                ? Sort.by(pageRequest.getSortBy()).ascending()
                : Sort.by(pageRequest.getSortBy()).descending();
            return find("status", sort, true).page(page).list();
        }
        
        return find("status", true).page(page).list();
    }

    /**
     * Contar total de usuarios
     */
    public long countAllUsers() {
        return count();
    }

    /**
     * Contar usuarios activos
     */
    public long countActiveUsers() {
        return count("status", true);
    }

    /**
     * Contar usuarios inactivos
     */
    public Long countInactiveUsers() {
        return count("status", false);
    }

    /**
     * Buscar usuarios por filtro con paginado
     */
    public List<User> findByFilterPaged(String filter, PageRequest pageRequest) {
        Page page = Page.of(pageRequest.getPage(), pageRequest.getSize());
        String query = "upper(username) like upper(?1) or upper(fullName) like upper(?1) or upper(email) like upper(?1)";
        String searchParam = "%" + filter + "%";
        
        if (pageRequest.isValidSort()) {
            Sort sort = pageRequest.isAscending() 
                ? Sort.by(pageRequest.getSortBy()).ascending()
                : Sort.by(pageRequest.getSortBy()).descending();
            return find(query, sort, searchParam).page(page).list();
        }
        
        return find(query, searchParam).page(page).list();
    }

    /**
     * Contar usuarios por filtro
     */
    public long countByFilter(String filter) {
        String query = "upper(username) like upper(?1) or upper(fullName) like upper(?1) or upper(email) like upper(?1)";
        String searchParam = "%" + filter + "%";
        return count(query, searchParam);
    }
}
