package com.grababite.backend.services;

import com.grababite.backend.models.AppSetting;
import com.grababite.backend.repositories.AppSettingRepository;
import com.grababite.backend.exceptions.ResourceNotFoundException; // Import ResourceNotFoundException
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppSettingService {

    @Autowired
    private AppSettingRepository appSettingRepository;

    /**
     * Retrieves all application settings.
     * @return A list of all AppSetting objects.
     */
    public List<AppSetting> getAllSettings() {
        return appSettingRepository.findAll();
    }

    /**
     * Retrieves a single application setting by its ID.
     * @param id The UUID of the setting.
     * @return An Optional containing the AppSetting if found.
     */
    public Optional<AppSetting> getSettingById(UUID id) {
        return appSettingRepository.findById(id);
    }

    /**
     * Retrieves a single application setting by its unique key.
     * @param settingKey The unique key of the setting (e.g., "SYSTEM_OPEN").
     * @return An Optional containing the AppSetting if found.
     */
    public Optional<AppSetting> getSettingByKey(String settingKey) {
        return appSettingRepository.findBySettingKey(settingKey);
    }

    /**
     * Creates a new application setting.
     * @param appSetting The AppSetting object to create.
     * @return The created AppSetting object.
     * @throws IllegalArgumentException if a setting with the same key already exists.
     */
    public AppSetting createSetting(AppSetting appSetting) {
        if (appSettingRepository.findBySettingKey(appSetting.getSettingKey()).isPresent()) {
            throw new IllegalArgumentException("Setting with key '" + appSetting.getSettingKey() + "' already exists.");
        }
        return appSettingRepository.save(appSetting);
    }

    /**
     * Updates an existing application setting.
     * @param id The UUID of the setting to update.
     * @param appSettingDetails The AppSetting object with updated details.
     * @return The updated AppSetting object.
     * @throws ResourceNotFoundException if the setting with the given ID is not found.
     * @throws IllegalArgumentException if the updated setting key already exists for another setting.
     */
    public AppSetting updateSetting(UUID id, AppSetting appSettingDetails) {
        return appSettingRepository.findById(id).map(existingSetting -> {
            // Check if the new key is different and already exists for another setting
            if (appSettingDetails.getSettingKey() != null &&
                !appSettingDetails.getSettingKey().equals(existingSetting.getSettingKey()) &&
                appSettingRepository.findBySettingKey(appSettingDetails.getSettingKey()).isPresent()) {
                throw new IllegalArgumentException("Setting with key '" + appSettingDetails.getSettingKey() + "' already exists for another setting.");
            }

            if (appSettingDetails.getSettingKey() != null) {
                existingSetting.setSettingKey(appSettingDetails.getSettingKey());
            }
            if (appSettingDetails.getSettingValue() != null) {
                existingSetting.setSettingValue(appSettingDetails.getSettingValue());
            }
            if (appSettingDetails.getDescription() != null) {
                existingSetting.setDescription(appSettingDetails.getDescription());
            }
            return appSettingRepository.save(existingSetting);
        }).orElseThrow(() -> new ResourceNotFoundException("App Setting not found with ID: " + id));
    }

    /**
     * Deletes an application setting by its ID.
     * @param id The UUID of the setting to delete.
     * @return true if the setting was deleted, false otherwise.
     */
    public boolean deleteSetting(UUID id) {
        if (appSettingRepository.existsById(id)) {
            appSettingRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Gets the value of a specific setting by its key.
     * @param settingKey The key of the setting.
     * @return The String value of the setting, or null if not found.
     */
    public String getSettingValue(String settingKey) {
        return appSettingRepository.findBySettingKey(settingKey)
                .map(AppSetting::getSettingValue)
                .orElse(null);
    }

    /**
     * Sets or updates the value of a specific setting by its key.
     * If the setting exists, its value is updated. If not, a new setting is created.
     * @param settingKey The key of the setting.
     * @param settingValue The new value for the setting.
     * @param description Optional description for new settings.
     * @return The updated or created AppSetting object.
     */
    public AppSetting setSettingValue(String settingKey, String settingValue, String description) {
        return appSettingRepository.findBySettingKey(settingKey)
                .map(existingSetting -> {
                    existingSetting.setSettingValue(settingValue);
                    return appSettingRepository.save(existingSetting);
                })
                .orElseGet(() -> appSettingRepository.save(new AppSetting(settingKey, settingValue, description)));
    }
}
