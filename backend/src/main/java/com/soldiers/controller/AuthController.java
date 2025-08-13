package com.soldiers.controller;

import com.soldiers.dto.request.LoginRequest;
import com.soldiers.dto.request.UserRequest;
import com.soldiers.dto.response.LoginResponse;
import com.soldiers.dto.response.UserResponse;
import com.soldiers.entity.User;
import com.soldiers.service.UserService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:8084", "http://127.0.0.1:8084", "http://172.18.0.3:8084"})
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint working");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.ok(new UserResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            List<UserResponse> userResponses = users.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(new UserResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        try {
            User user = userService.updateUser(id, request);
            return ResponseEntity.ok(new UserResponse(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/users/{id}/activate")
    public ResponseEntity<String> activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok("Usuário ativado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao ativar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/users/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok("Usuário desativado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao desativar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/init-data")
    public ResponseEntity<String> initData() {
        try {
            userService.initData();
            return ResponseEntity.ok("Dados iniciais inseridos com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao inicializar dados: " + e.getMessage());
        }
    }

    @PostMapping("/reset-data")
    public ResponseEntity<String> resetData() {
        try {
            userService.resetData();
            return ResponseEntity.ok("Dados resetados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao resetar dados: " + e.getMessage());
        }
    }

    @PostMapping("/create-test-users")
    public ResponseEntity<String> createTestUsers() {
        try {
            userService.createTestUsers();
            return ResponseEntity.ok("Usuários de teste criados com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar usuários de teste: " + e.getMessage());
        }
    }

    @GetMapping("/generate-password/{password}")
    public ResponseEntity<String> generatePassword(@PathVariable String password) {
        try {
            String encodedPassword = userService.encodePassword(password);
            return ResponseEntity.ok(encodedPassword);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao gerar senha: " + e.getMessage());
        }
    }
} 
