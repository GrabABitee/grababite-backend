package com.grababite.backend.services;

import com.grababite.backend.models.StandardMenuItem;
import com.grababite.backend.repositories.StandardMenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StandardMenuItemService {

    @Autowired
    private StandardMenuItemRepository standardMenuItemRepository;

    /**
     * Retrieves all standard menu items.
     * @return A list of StandardMenuItem objects.
     */
    public List<StandardMenuItem> getAllStandardMenuItems() {
        return standardMenuItemRepository.findAll();
    }

    /**
     * Retrieves a single standard menu item by its ID.
     * @param id The UUID of the standard menu item to retrieve.
     * @return An Optional containing the StandardMenuItem if found, or empty if not.
     */
    public Optional<StandardMenuItem> getStandardMenuItemById(UUID id) {
        return standardMenuItemRepository.findById(id);
    }

    /**
     * Creates a new standard menu item.
     * @param standardMenuItem The StandardMenuItem object to save.
     * @return The saved StandardMenuItem object.
     */
    public StandardMenuItem createStandardMenuItem(StandardMenuItem standardMenuItem) {
        return standardMenuItemRepository.save(standardMenuItem);
    }

    /**
     * Updates an existing standard menu item.
     * @param id The UUID of the standard menu item to update.
     * @param standardMenuItemDetails The StandardMenuItem object with updated details.
     * @return The updated StandardMenuItem object, or null if not found.
     */
    public StandardMenuItem updateStandardMenuItem(UUID id, StandardMenuItem standardMenuItemDetails) {
        return standardMenuItemRepository.findById(id).map(item -> {
            item.setName(standardMenuItemDetails.getName());
            item.setDescription(standardMenuItemDetails.getDescription());
            item.setImageUrl(standardMenuItemDetails.getImageUrl());
            return standardMenuItemRepository.save(item);
        }).orElse(null);
    }

    /**
     * Deletes a standard menu item by its ID.
     * @param id The UUID of the standard menu item to delete.
     * @return true if deleted, false otherwise.
     */
    public boolean deleteStandardMenuItem(UUID id) {
        if (standardMenuItemRepository.existsById(id)) {
            standardMenuItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
