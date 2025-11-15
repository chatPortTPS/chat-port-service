package com.tps.orm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tps.orm.dto.PageRequest;
import com.tps.orm.dto.PagedResponse;
import com.tps.orm.entity.User;
import com.tps.orm.repository.AreasUsuarioRepository;
import com.tps.orm.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import services.operacion.user.UserResponse;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Inject
    AreasUsuarioRepository areasUsuarioRepository;

    /**
     * Crear nuevo usuario
     */
    @Transactional
    public UserResponse createUser(String username, String email) {
        // Validar que no exista el usuario
        if (username == null || username.isEmpty() || email == null || email.isEmpty()) {
            throw new IllegalArgumentException("El username y el email no deben estar vacíos");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El usuario: " + username + " ya existe.");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email: " + email + " ya existe.");
        }

        // Validar formato de email
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setAccessedAt(LocalDateTime.now());

        userRepository.persist(user);

        UserResponse userResponse = convertToUserResponse(user);
      
        return userResponse;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
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
    @Transactional
    public Optional<User> findById(Long id) {
        User user = userRepository.findById(id);
        return Optional.ofNullable(user);
    }

    /**
     * Obtener usuario por ID y mapear a UserResponse
     */
    @Transactional
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return convertToUserResponse(user);
    }

    /**
     * Buscar usuario por username
     */
    @Transactional
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Obtener usuario por username y mapear a UserResponse
     */
    @Transactional
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        if(!user.getStatus()){
            throw new IllegalArgumentException("Usuario inactivo");
        }

        return convertToUserResponse(user);
    }

    /**
     * Obtener usuario por username y mapear a UserResponse
     */
    @Transactional
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + email));

        if(!user.getStatus()){
            throw new IllegalArgumentException("Usuario inactivo");
        }

        return convertToUserResponse(user);
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

    /**
     * Marcar ingreso de usuario (actualizar lastAccessedAt)
     */
    @Transactional
    public void incomeUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAccessedAt(LocalDateTime.now());
            userRepository.persist(user);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado: " + username);
        }
    }
 

    /**
     * Obtener total de usuarios
     */
    @Transactional
    public long getTotalUsers() {
        return userRepository.countAllUsers();
    }

    /**
     * Obtener total de usuarios activos
     */
    @Transactional
    public long getActiveUsers() {
        return userRepository.countActiveUsers();
    }

    /**
     * Obtener total de usuarios inactivos
     */
    @Transactional
    public long getInactiveUsers() {
        return userRepository.countInactiveUsers();
    }

    /**
     * Obtener todos los usuarios con paginado
     */
    @Transactional
    public PagedResponse<UserResponse> findAllUsersPaged(PageRequest pageRequest) {
        List<User> users = userRepository.findAllPaged(pageRequest);
        long totalElements = userRepository.countAllUsers();
        
        List<UserResponse> userResponses = users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
        
        return PagedResponse.of(userResponses, pageRequest.getPage(), pageRequest.getSize(), totalElements);
    }

    /**
     * Obtener usuarios activos con paginado
     */
    @Transactional
    public PagedResponse<UserResponse> findActiveUsersPaged(PageRequest pageRequest) {
        List<User> users = userRepository.findActiveUsersPaged(pageRequest);
        long totalElements = userRepository.countActiveUsers();
        
        List<UserResponse> userResponses = users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
        
        return PagedResponse.of(userResponses, pageRequest.getPage(), pageRequest.getSize(), totalElements);
    }

    /**
     * Buscar usuarios por filtro con paginado
     */
    @Transactional
    public PagedResponse<UserResponse> findUsersByFilterPaged(String filter, PageRequest pageRequest) {
        if (filter == null || filter.trim().isEmpty()) {
            return findAllUsersPaged(pageRequest);
        }
        
        List<User> users = userRepository.findByFilterPaged(filter, pageRequest);
        long totalElements = userRepository.countByFilter(filter);
        
        List<UserResponse> userResponses = users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
        
        return PagedResponse.of(userResponses, pageRequest.getPage(), pageRequest.getSize(), totalElements);
    }

    /**
     * Convertir User a UserResponse (método helper)
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt().toString());
        return response;
    }

    @Transactional
    public List<String> getUserAreasByEmail(String email) {
       
        List<String> areas = areasUsuarioRepository.find("correo", email)
                .stream()
                .map(areaUsuario -> areaUsuario.getNombreNormalizado())
                .collect(Collectors.toList());

        return areas;
        
    }
 
}
