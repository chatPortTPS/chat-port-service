package com.tps.orm.service;

import com.tps.orm.entity.User;
import com.tps.orm.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import services.operacion.user.UserResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    /**
     * Crear nuevo usuario
     */
    @Transactional
    public UserResponse createUser(String username, String email) {
        // Validar que no exista el usuario
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setAccessedAt(LocalDateTime.now());

        userRepository.persist(user);


        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setStatus(user.getStatus());
        userResponse.setCreatedAt(user.getCreatedAt());

        return userResponse;
    }

    /**
     * Actualizar usuario
     */
    @Transactional
    public User updateUser(Long userId, String fullName, String email) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        // Verificar si el email ya existe (y no es del mismo usuario)
        Optional<User> existingEmailUser = userRepository.findByEmail(email);
        if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(userId)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
 
        user.setEmail(email); 

        userRepository.persist(user);
        return user;
    }

    /**
     * Buscar usuario por ID
     */
    public Optional<User> findById(Long id) {
        User user = userRepository.findById(id);
        return Optional.ofNullable(user);
    }

    /**
     * Buscar usuario por username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Buscar usuario por email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Obtener todos los usuarios activos
     */
    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }

    /**
     * Obtener todos los usuarios
     */
    public List<User> findAllUsers() {
        return userRepository.listAll();
    }

    /**
     * Activar usuario
     */
    @Transactional
    public void activateUser(Long userId) {
        userRepository.updateUserStatus(userId, true);
    }

    /**
     * Desactivar usuario
     */
    @Transactional
    public void deactivateUser(Long userId) {
        userRepository.updateUserStatus(userId, false);
    }

    /**
     * Eliminar usuario
     */
    @Transactional
    public boolean deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            userRepository.delete(user);
            return true;
        }
        return false;
    }
 
}
