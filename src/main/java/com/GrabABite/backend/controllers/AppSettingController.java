package com.grababite.backend.controllers;

import com.grababite.backend.models.AppSetting;
import com.grababite.backend.services.AppSettingService;
import com.grababite.backend.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; // Import for method-level security

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/app-settings")
public class AppSettingController {

    @Autowired
    private AppSettingService appSettingService;

    /**
     * GET /api/app-settings
     * Retrieves all application settings.
     * Restricted to ADMIN role.
     * @return A list of all AppSetting objects.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can view all settings
    public List<AppSetting> getAllSettings() {
        return appSettingService.getAllSettings();
    }

    /**
     * GET /api/app-settings/{id}
     * Retrieves a single application setting by its ID.
     * Restricted to ADMIN role.
     * @param id The UUID of the setting to retrieve.
     * @return ResponseEntity with the AppSetting object and HTTP status 200 OK,
     * or 404 Not Found if the setting does not exist.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can view a specific setting by ID
    public ResponseEntity<AppSetting> getSettingById(@PathVariable UUID id) {
        return appSettingService.getSettingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/app-settings/key/{settingKey}
     * Retrieves a single application setting by its unique key.
     * Restricted to ADMIN role.
     * @param settingKey The unique key of the setting (e.g., "SYSTEM_OPEN").
     * @return ResponseEntity with the AppSetting object and HTTP status 200 OK,
     * or 404 Not Found if the setting does not exist.
     */
    @GetMapping("/key/{settingKey}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can view a specific setting by key
    public ResponseEntity<AppSetting> getSettingByKey(@PathVariable String settingKey) {
        return appSettingService.getSettingByKey(settingKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/app-settings
     * Creates a new application setting.
     * Restricted to ADMIN role.
     * @param appSetting The AppSetting object to create (sent in the request body as JSON).
     * @return ResponseEntity with the created AppSetting object and HTTP status 201 Created.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can create settings
    public ResponseEntity<AppSetting> createSetting(@RequestBody AppSetting appSetting) {
        try {
            AppSetting createdSetting = appSettingService.createSetting(appSetting);
            return new ResponseEntity<>(createdSetting, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict if key already exists
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * PUT /api/app-settings/{id}
     * Updates an existing application setting.
     * Restricted to ADMIN role.
     * @param id The UUID of the setting to update.
     * @param appSettingDetails The AppSetting object with updated details (sent in request body).
     * @return ResponseEntity with the updated AppSetting object and HTTP status 200 OK,
     * or 404 Not Found if the setting does not exist, or 409 Conflict if key already exists.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can update settings
    public ResponseEntity<AppSetting> updateSetting(@PathVariable UUID id, @RequestBody AppSetting appSettingDetails) {
        try {
            AppSetting updatedSetting = appSettingService.updateSetting(id, appSettingDetails);
            return ResponseEntity.ok(updatedSetting);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict if new key already exists
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * DELETE /api/app-settings/{id}
     * Deletes an application setting by its ID.
     * Restricted to ADMIN role.
     * @param id The UUID of the setting to delete.
     * @return ResponseEntity with HTTP status 204 No Content if deleted,
     * or 404 Not Found if the setting does not exist.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can delete settings
    public ResponseEntity<HttpStatus> deleteSetting(@PathVariable UUID id) {
        boolean deleted = appSettingService.deleteSetting(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
