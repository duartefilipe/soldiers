package com.soldiers.controller;

import com.soldiers.dto.request.ProfileRequest;
import com.soldiers.dto.response.ProfileResponse;
import com.soldiers.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<ProfileResponse>> getAllProfiles() {
        List<ProfileResponse> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/non-admin")
    public ResponseEntity<List<ProfileResponse>> getNonAdminProfiles() {
        List<ProfileResponse> profiles = profileService.getNonAdminProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable Long id) {
        return profileService.getProfileWithPermissions(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<String>> getUsersByProfile(@PathVariable Long id) {
        try {
            List<String> users = profileService.getUsersByProfile(id);
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(List.of("Erro: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody ProfileRequest request) {
        try {
            ProfileResponse profile = profileService.createProfile(request);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponse> updateProfile(@PathVariable Long id, @Valid @RequestBody ProfileRequest request) {
        try {
            ProfileResponse profile = profileService.updateProfile(id, request);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateProfile(@PathVariable Long id) {
        try {
            profileService.deactivateProfile(id);
            return ResponseEntity.ok("Perfil desativado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao desativar perfil: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        try {
            profileService.deleteProfile(id);
            return ResponseEntity.ok("Perfil deletado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao deletar perfil: " + e.getMessage());
        }
    }



    @PostMapping("/initialize")
    public ResponseEntity<String> initializeDefaultProfiles() {
        try {
            profileService.initializeDefaultProfiles();
            return ResponseEntity.ok("Perfis padrão inicializados com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao inicializar perfis: " + e.getMessage());
        }
    }
}
