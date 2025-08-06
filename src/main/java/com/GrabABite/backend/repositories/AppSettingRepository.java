package com.grababite.backend.repositories;

import com.grababite.backend.models.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, UUID> {
    // Custom method to find a setting by its unique key
    Optional<AppSetting> findBySettingKey(String settingKey);
}
