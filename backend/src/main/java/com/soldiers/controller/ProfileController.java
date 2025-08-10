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
        return profileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        try {
            profileService.deleteProfile(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
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
