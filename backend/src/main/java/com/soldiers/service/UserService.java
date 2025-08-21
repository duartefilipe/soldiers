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
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ProfileService profileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            System.out.println("Tentando fazer login para o email: " + request.getEmail());
            
            // Buscar usuário com perfis e permissões carregadas
            User user = userRepository.findByEmailWithProfilesAndPermissions(request.getEmail())
                    .orElseGet(() -> {
                        // Se não encontrar com perfil, tentar sem perfil
                        return userRepository.findByEmailAndDeletadoEmIsNull(request.getEmail())
                                .orElse(null);
                    });
            
            if (user == null) {
                throw new RuntimeException("Usuário não encontrado");
            }

            System.out.println("Usuário encontrado: " + user.getName());

            // Verificar se o usuário está ativo
            if (!user.isActive()) {
                throw new RuntimeException("Usuário inativo");
            }

            System.out.println("Usuário está ativo");

            // Verificar senha
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Senha incorreta");
            }

            System.out.println("Senha verificada com sucesso");

            return new LoginResponse("SUCCESS", user);
        } catch (Exception e) {
            System.err.println("Erro no login: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public User createUser(UserRequest request) {
        // Verificar se o email já existe para usuários ativos
        Optional<User> existingUser = userRepository.findByEmailAndDeletadoEmIsNull(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        // Adicionar perfis se fornecidos
        if (request.getProfileName() != null && profileService != null) {
            Profile profile = profileService.getProfileByName(request.getProfileName())
                    .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
            user.addProfile(profile);
        }
        
        // Adicionar múltiplos perfis se fornecidos
        if (request.getProfileNames() != null && !request.getProfileNames().isEmpty() && profileService != null) {
            for (String profileName : request.getProfileNames()) {
                Profile profile = profileService.getProfileByName(profileName)
                        .orElseThrow(() -> new RuntimeException("Perfil não encontrado: " + profileName));
                user.addProfile(profile);
            }
        }

        user = userRepository.save(user);
        
        // Retornar o usuário com perfil carregado
        return userRepository.findByIdWithProfile(user.getId())
                .orElse(user);
    }

    public List<User> getAllUsers() {
        // Usar uma query que carrega todos os usuários com seus perfis de uma vez
        // para evitar o problema N+1 e duplicação
        return userRepository.findAllActiveWithProfiles();
    }

    public User getUserById(Long id) {
        return userRepository.findByIdWithProfile(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public User findByIdWithProfilesAndPermissions(Long id) {
        return userRepository.findByIdWithProfilesAndPermissions(id)
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

        // Atualizar perfis se fornecidos
        System.out.println("ProfileService é null? " + (profileService == null));
        System.out.println("ProfileName fornecido: " + request.getProfileName());
        System.out.println("ProfileNames fornecido: " + request.getProfileNames());
        
        // Limpar perfis existentes
        user.getProfiles().clear();
        
        // Adicionar perfis por profileName (compatibilidade)
        if (request.getProfileName() != null && profileService != null) {
            System.out.println("Buscando perfil: " + request.getProfileName());
            Profile profile = profileService.getProfileByName(request.getProfileName())
                    .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
            System.out.println("Perfil encontrado: " + profile.getName() + " (ID: " + profile.getId() + ")");
            user.addProfile(profile);
        }
        
        // Adicionar perfis por profileNames (múltiplos perfis)
        if (request.getProfileNames() != null && !request.getProfileNames().isEmpty() && profileService != null) {
            for (String profileName : request.getProfileNames()) {
                System.out.println("Buscando perfil: " + profileName);
                Profile profile = profileService.getProfileByName(profileName)
                        .orElseThrow(() -> new RuntimeException("Perfil não encontrado: " + profileName));
                System.out.println("Perfil encontrado: " + profile.getName() + " (ID: " + profile.getId() + ")");
                user.addProfile(profile);
            }
        }
        
        if (user.getProfiles().isEmpty()) {
            System.out.println("Nenhum perfil foi atribuído ao usuário");
        }

        user = userRepository.save(user);
        
        // Retornar o usuário com perfil carregado
        return userRepository.findByIdWithProfile(user.getId())
                .orElse(user);
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

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmailAndDeletadoEmIsNull(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void initData() {
        try {
            // Verificar se já existem usuários
            if (userRepository.count() == 0) {
                // Criar usuário admin sem perfil
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@soldiers.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setProfile(null); // Garantir que o profile seja null
                admin.setActive(true);
                userRepository.save(admin);

                // Criar usuário Anakin sem perfil
                User anakin = new User();
                anakin.setName("Anakin");
                anakin.setEmail("anakin@anakin.com");
                anakin.setPassword(passwordEncoder.encode("eumesmo"));
                anakin.setProfile(null); // Garantir que o profile seja null
                anakin.setActive(true);
                userRepository.save(anakin);
            }
        } catch (Exception e) {
            System.err.println("Erro ao inicializar dados: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void resetData() {
        // Limpar todos os usuários de forma segura
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.setDeletadoEm(LocalDateTime.now());
            userRepository.save(user);
        }
        
        // Criar usuário admin sem perfil por enquanto
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@soldiers.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setActive(true);
        userRepository.save(admin);

        // Criar usuário Anakin
        User anakin = new User();
        anakin.setName("Anakin");
        anakin.setEmail("anakin@anakin.com");
        anakin.setPassword(passwordEncoder.encode("eumesmo"));
        anakin.setActive(true);
        userRepository.save(anakin);
    }

    public void createTestUsers() {
        // Verificar se o usuário admin já existe
        if (!userRepository.existsByEmail("admin@soldiers.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@soldiers.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);
            userRepository.save(admin);
        }

        // Verificar se o usuário Anakin já existe
        if (!userRepository.existsByEmail("anakin@anakin.com")) {
            User anakin = new User();
            anakin.setName("Anakin");
            anakin.setEmail("anakin@anakin.com");
            anakin.setPassword(passwordEncoder.encode("eumesmo"));
            anakin.setActive(true);
            userRepository.save(anakin);
        }
    }

    public void initDataWithProfiles() {
        // Inicializar perfis padrão primeiro
        if (profileService != null) {
            profileService.initializeDefaultProfiles();
        }

        // Verificar se já existem usuários
        if (userRepository.count() == 0) {
            // Buscar perfil ADMIN
            Profile adminProfile = null;
            if (profileService != null) {
                adminProfile = profileService.getProfileByName("ADMIN")
                        .orElse(null);
            }

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
