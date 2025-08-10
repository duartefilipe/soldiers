package com.soldiers.repository;

import com.soldiers.entity.ProfilePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfilePermissionRepository extends JpaRepository<ProfilePermission, Long> {

    List<ProfilePermission> findByProfileId(Long profileId);
    
    List<ProfilePermission> findByProfileIdAndActiveTrue(Long profileId);
    
    @Query("SELECT pp FROM ProfilePermission pp WHERE pp.profile.id = :profileId AND pp.resource = :resource AND pp.active = true")
    List<ProfilePermission> findByProfileIdAndResource(@Param("profileId") Long profileId, @Param("resource") String resource);
    
    @Query("SELECT pp FROM ProfilePermission pp WHERE pp.profile.id = :profileId AND pp.resource = :resource AND pp.action = :action AND pp.active = true")
    ProfilePermission findByProfileIdAndResourceAndAction(@Param("profileId") Long profileId, @Param("resource") String resource, @Param("action") String action);
    
    void deleteByProfileId(Long profileId);
}
