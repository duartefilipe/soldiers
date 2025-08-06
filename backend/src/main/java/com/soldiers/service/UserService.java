package com.soldiers.service;

import com.soldiers.dto.request.LoginRequest;
import com.soldiers.dto.request.UserRequest;
import com.soldiers.dto.response.LoginResponse;
import com.soldiers.entity.User;
import com.soldiers.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletadoEmIsNull(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificar senha
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha incorreta");
        }

        return new LoginResponse("SUCCESS", user);
    }

    public User createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAllActive();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public User updateUser(Long id, UserRequest request) {
        User user = getUserById(id);
        
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setDeletadoEm(LocalDateTime.now());
        userRepository.save(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmailAndDeletadoEmIsNull(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void initData() {
        // Verificar se já existem usuários
        if (userRepository.count() == 0) {
            // Criar usuário admin
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@soldiers.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);

            // Criar usuário Anakin
            User anakin = new User();
            anakin.setName("Anakin");
            anakin.setEmail("anakin@anakin.com");
            anakin.setPassword(passwordEncoder.encode("eumesmo"));
            anakin.setRole(User.UserRole.ADMIN);
            userRepository.save(anakin);
        }
    }


} 
