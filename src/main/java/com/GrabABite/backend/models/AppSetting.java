package com.grababite.backend.models;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "app_settings")
public class AppSetting extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "setting_id", nullable = false, unique = true)
    private UUID settingId;

    @Column(name = "setting_key", nullable = false, unique = true)
    private String settingKey; // e.g., "SYSTEM_OPEN", "DEFAULT_DELIVERY_FEE"

    @Column(name = "setting_value", nullable = false)
    private String settingValue; // e.g., "true", "5.00"

    @Column(name = "description")
    private String description; // Optional: for explaining the setting

    // Constructors
    public AppSetting() {
    }

    public AppSetting(String settingKey, String settingValue, String description) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
    }

    // Getters and Setters
    public UUID getSettingId() {
        return settingId;
    }

    public void setSettingId(UUID settingId) {
        this.settingId = settingId;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
