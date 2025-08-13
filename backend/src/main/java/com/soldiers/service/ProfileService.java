package com.soldiers.service;

import com.soldiers.dto.request.ProfileRequest;
import com.soldiers.dto.response.ProfileResponse;
import com.soldiers.entity.Profile;
import com.soldiers.entity.ProfilePermission;
import com.soldiers.repository.ProfilePermissionRepository;
import com.soldiers.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfilePermissionRepository permissionRepository;

    public List<ProfileResponse> getAllProfiles() {
        return profileRepository.findAllActiveWithPermissions()
                .stream()
                .map(ProfileResponse::new)
                .collect(Collectors.toList());
    }

    public List<ProfileResponse> getNonAdminProfiles() {
        return profileRepository.findNonAdminProfiles()
                .stream()
                .map(ProfileResponse::new)
                .collect(Collectors.toList());
    }

    public Optional<ProfileResponse> getProfileById(Long id) {
        return profileRepository.findById(id)
                .map(ProfileResponse::new);
    }

    public Optional<ProfileResponse> getProfileWithPermissions(Long id) {
        return profileRepository.findByIdWithPermissions(id)
                .map(ProfileResponse::new);
    }

    public Optional<Profile> getProfileByName(String name) {
        return profileRepository.findByName(name);
    }

    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        if (profileRepository.existsByName(request.getName())) {
            throw new RuntimeException("Perfil com este nome já existe");
        }

        Profile profile = new Profile();
        profile.setName(request.getName());
        profile.setDescription(request.getDescription());
        profile.setActive(request.getActive());

        profile = profileRepository.save(profile);

        // Adicionar permissões se fornecidas
        if (request.getPermissions() != null) {
            for (ProfileRequest.PermissionRequest permRequest : request.getPermissions()) {
                ProfilePermission permission = new ProfilePermission();
                permission.setProfile(profile);
                permission.setResource(permRequest.getResource());
                permission.setAction(permRequest.getAction());
                permission.setActive(permRequest.getActive());
                permissionRepository.save(permission);
            }
        }

        // Forçar flush da transação
        profileRepository.flush();
        permissionRepository.flush();

        // Retornar o perfil com as permissões carregadas
        return profileRepository.findByIdWithPermissions(profile.getId())
                .map(ProfileResponse::new)
                .orElse(new ProfileResponse(profile));
    }

    @Transactional
    public ProfileResponse updateProfile(Long id, ProfileRequest request) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));

        // Verificar se o nome já existe em outro perfil
        if (!profile.getName().equals(request.getName()) && 
            profileRepository.existsByName(request.getName())) {
            throw new RuntimeException("Perfil com este nome já existe");
        }

        profile.setName(request.getName());
        profile.setDescription(request.getDescription());
        profile.setActive(request.getActive());

        profile = profileRepository.save(profile);

        // Atualizar permissões
        if (request.getPermissions() != null) {
            // Remover permissões existentes
            List<ProfilePermission> existingPermissions = permissionRepository.findByProfileId(id);
            permissionRepository.deleteAll(existingPermissions);

            // Adicionar novas permissões
            for (ProfileRequest.PermissionRequest permRequest : request.getPermissions()) {
                ProfilePermission permission = new ProfilePermission();
                permission.setProfile(profile);
                permission.setResource(permRequest.getResource());
                permission.setAction(permRequest.getAction());
                permission.setActive(permRequest.getActive());
                permissionRepository.save(permission);
            }
        }

        // Forçar flush da transação
        profileRepository.flush();
        permissionRepository.flush();

        // Retornar o perfil com as permissões carregadas
        return profileRepository.findByIdWithPermissions(profile.getId())
                .map(ProfileResponse::new)
                .orElse(new ProfileResponse(profile));
    }

    @Transactional
    public void deleteProfile(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));

        if ("ADMIN".equals(profile.getName())) {
            throw new RuntimeException("Não é possível deletar o perfil ADMIN");
        }

        // Verificar se há usuários usando este perfil
        if (!profile.getUsers().isEmpty()) {
            throw new RuntimeException("Não é possível deletar perfil que possui usuários associados");
        }

        profile.setActive(false);
        profileRepository.save(profile);
    }

    @Transactional
    public void initializeDefaultProfiles() {
        // Criar perfil ADMIN se não existir
        if (!profileRepository.existsByName("ADMIN")) {
            Profile adminProfile = new Profile("ADMIN", "Administrador do sistema com acesso total");
            adminProfile = profileRepository.save(adminProfile);

            // Adicionar todas as permissões para ADMIN
            String[] resources = {"DASHBOARD", "USERS", "PRODUCTS", "SALES", "TRIPS", "NEWS", "BUDGET", "GAMES"};
            String[] actions = {"VIEW", "EDIT"};

            for (String resource : resources) {
                for (String action : actions) {
                    ProfilePermission permission = new ProfilePermission(adminProfile, resource, action);
                    permissionRepository.save(permission);
                }
            }
        }

        // Criar perfil VENDEDOR se não existir
        if (!profileRepository.existsByName("VENDEDOR")) {
            Profile vendedorProfile = new Profile("VENDEDOR", "Vendedor com acesso a vendas e produtos");
            vendedorProfile = profileRepository.save(vendedorProfile);

            // Permissões para VENDEDOR
            String[] vendedorResources = {"DASHBOARD", "PRODUCTS", "SALES"};
            String[] vendedorActions = {"VIEW", "EDIT"};

            for (String resource : vendedorResources) {
                for (String action : vendedorActions) {
                    ProfilePermission permission = new ProfilePermission(vendedorProfile, resource, action);
                    permissionRepository.save(permission);
                }
            }
        }

        // Criar perfil VISUALIZADOR se não existir
        if (!profileRepository.existsByName("VISUALIZADOR")) {
            Profile visualizadorProfile = new Profile("VISUALIZADOR", "Usuário com acesso apenas para visualização");
            visualizadorProfile = profileRepository.save(visualizadorProfile);

            // Permissões apenas de visualização
            String[] visualizadorResources = {"DASHBOARD", "PRODUCTS", "SALES", "TRIPS", "NEWS", "GAMES"};

            for (String resource : visualizadorResources) {
                ProfilePermission permission = new ProfilePermission(visualizadorProfile, resource, "VIEW");
                permissionRepository.save(permission);
            }
        }
    }

    public boolean hasPermission(Long profileId, String resource, String action) {
        ProfilePermission permission = permissionRepository.findByProfileIdAndResourceAndAction(profileId, resource, action);
        return permission != null && permission.isActive();
    }
}
