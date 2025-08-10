package com.soldiers.service;

import com.soldiers.dto.request.LoginRequest;
import com.soldiers.dto.request.UserRequest;
import com.soldiers.dto.response.LoginResponse;
import com.soldiers.entity.Profile;
import com.soldiers.entity.User;
import com.soldiers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ProfileService profileService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndDeletadoEmIsNull(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificar se o usuário está ativo
        if (!user.isActive()) {
            throw new RuntimeException("Usuário inativo");
        }

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

        // Buscar o perfil
        Profile profile = profileService.getProfileByName(request.getProfileName())
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProfile(profile);
        user.setActive(true);

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

        // Atualizar perfil se fornecido
        if (request.getProfileName() != null) {
            Profile profile = profileService.getProfileByName(request.getProfileName())
                    .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
            user.setProfile(profile);
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        user.setDeletadoEm(LocalDateTime.now());
        userRepository.save(user);
    }

    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = getUserById(id);
        user.setActive(true);
        userRepository.save(user);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmailAndDeletadoEmIsNull(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void initData() {
        // Inicializar perfis padrão primeiro
        profileService.initializeDefaultProfiles();

        // Verificar se já existem usuários
        if (userRepository.count() == 0) {
            // Buscar perfil ADMIN
            Profile adminProfile = profileService.getProfileByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Perfil ADMIN não encontrado"));

            // Criar usuário admin
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@soldiers.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setProfile(adminProfile);
            admin.setActive(true);
            userRepository.save(admin);

            // Criar usuário Anakin
            User anakin = new User();
            anakin.setName("Anakin");
            anakin.setEmail("anakin@anakin.com");
            anakin.setPassword(passwordEncoder.encode("eumesmo"));
            anakin.setProfile(adminProfile);
            anakin.setActive(true);
            userRepository.save(anakin);
        }
    }
} 
