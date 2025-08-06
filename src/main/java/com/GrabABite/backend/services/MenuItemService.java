package com.grababite.backend.services;

import com.grababite.backend.models.Cafeteria;
import com.grababite.backend.models.MenuItem;
import com.grababite.backend.models.StandardMenuItem; // NEW: Import StandardMenuItem
import com.grababite.backend.repositories.CafeteriaRepository;
import com.grababite.backend.repositories.MenuItemRepository;
import com.grababite.backend.repositories.StandardMenuItemRepository; // NEW: Import StandardMenuItemRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CafeteriaRepository cafeteriaRepository;

    @Autowired
    private StandardMenuItemRepository standardMenuItemRepository; // NEW: Autowire StandardMenuItemRepository

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public Optional<MenuItem> getMenuItemById(UUID id) {
        return menuItemRepository.findById(id);
    }

    /**
     * Creates a new menu item.
     * This method now handles both standard menu item selection and custom menu item creation.
     * If standardMenuItemId is provided, it populates name, description, and imageUrl from it.
     * Price and availability are always taken from the MenuItem object passed.
     * @param menuItem The MenuItem object to create (with cafeteria, price, isAvailable set).
     * @param standardMenuItemId Optional UUID of the StandardMenuItem to link. If null, it's a custom item.
     * @return The created MenuItem object.
     * @throws IllegalArgumentException if a standardMenuItemId is provided but not found.
     */
    public MenuItem createMenuItem(MenuItem menuItem, UUID standardMenuItemId) {
        if (standardMenuItemId != null) {
            StandardMenuItem standardItem = standardMenuItemRepository.findById(standardMenuItemId)
                    .orElseThrow(() -> new IllegalArgumentException("Standard menu item not found with ID: " + standardMenuItemId));
            menuItem.setStandardMenuItem(standardItem); // Link to the standard item
            // Populate details from standard item, but allow price/availability to be custom
            menuItem.setName(standardItem.getName());
            menuItem.setDescription(standardItem.getDescription());
            menuItem.setImageUrl(standardItem.getImageUrl());
        }
        return menuItemRepository.save(menuItem);
    }

    /**
     * Updates an existing menu item.
     * @param id The UUID of the menu item to update.
     * @param menuItemDetails The MenuItem object with updated details.
     * @return The updated MenuItem object, or null if not found.
     */
    public MenuItem updateMenuItem(UUID id, MenuItem menuItemDetails) {
        return menuItemRepository.findById(id).map(menuItem -> {
            menuItem.setName(menuItemDetails.getName());
            menuItem.setDescription(menuItemDetails.getDescription());
            menuItem.setPrice(menuItemDetails.getPrice());
            menuItem.setIsAvailable(menuItemDetails.getIsAvailable());
            menuItem.setImageUrl(menuItemDetails.getImageUrl());

            // Update cafeteria relationship if provided in details
            if (menuItemDetails.getCafeteria() != null) {
                Optional<Cafeteria> existingCafeteria = cafeteriaRepository.findById(menuItemDetails.getCafeteria().getCafeteriaId());
                existingCafeteria.ifPresent(menuItem::setCafeteria);
            }

            // Update standard menu item relationship if provided in details
            // If standardMenuItem is explicitly set to null in details, clear the link
            if (menuItemDetails.getStandardMenuItem() != null && menuItemDetails.getStandardMenuItem().getStandardMenuItemId() != null) {
                Optional<StandardMenuItem> existingStandardItem = standardMenuItemRepository.findById(menuItemDetails.getStandardMenuItem().getStandardMenuItemId());
                existingStandardItem.ifPresent(menuItem::setStandardMenuItem);
            } else if (menuItemDetails.getStandardMenuItem() == null) {
                menuItem.setStandardMenuItem(null);
            }

            return menuItemRepository.save(menuItem);
        }).orElse(null);
    }

    public boolean deleteMenuItem(UUID id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
