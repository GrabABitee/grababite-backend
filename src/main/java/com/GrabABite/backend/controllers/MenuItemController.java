package com.grababite.backend.controllers;

import com.grababite.backend.dto.MenuItemCreationRequest;
import com.grababite.backend.models.Cafeteria;
import com.grababite.backend.models.MenuItem;
import com.grababite.backend.services.CafeteriaService;
import com.grababite.backend.services.MenuItemService;
import com.grababite.backend.services.UserService; // NEW: Import UserService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; // NEW: Import PreAuthorize

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private CafeteriaService cafeteriaService;

    @Autowired
    private UserService userService; // NEW: Autowire UserService for security checks

    @GetMapping
    public List<MenuItem> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable UUID id) {
        return menuItemService.getMenuItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/menu-items
     * Creates a new menu item for a cafeteria.
     * Access Control: ADMIN or CAFETERIA_OWNER (for their own cafeteria).
     *
     * @param request The MenuItemCreationRequest DTO.
     * @return ResponseEntity with the created MenuItem object and HTTP status 201 Created,
     * or 400 Bad Request if cafeteria or standard menu item not found, or if required fields are missing.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CAFETERIA_OWNER') and @userService.getCurrentUserCafeteriaId() == #request.cafeteriaId)")
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItemCreationRequest request) {
        // 1. Validate Cafeteria
        Optional<Cafeteria> cafeteriaOptional = cafeteriaService.getCafeteriaById(request.getCafeteriaId());
        if (cafeteriaOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Cafeteria not found
        }

        // 2. Prepare MenuItem entity
        MenuItem menuItem = new MenuItem();
        menuItem.setCafeteria(cafeteriaOptional.get());
        menuItem.setPrice(request.getPrice());
        menuItem.setIsAvailable(request.getIsAvailable());

        // Handle either standard item selection or custom item details
        if (request.getStandardMenuItemId() != null) {
            try {
                MenuItem createdMenuItem = menuItemService.createMenuItem(menuItem, request.getStandardMenuItemId());
                return new ResponseEntity<>(createdMenuItem, HttpStatus.CREATED);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Standard item not found
            }
        } else {
            if (request.getName() == null || request.getName().isEmpty() || request.getPrice() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Missing required fields for custom item
            }
            menuItem.setName(request.getName());
            menuItem.setDescription(request.getDescription());
            menuItem.setImageUrl(request.getImageUrl());
            MenuItem createdMenuItem = menuItemService.createMenuItem(menuItem, null);
            return new ResponseEntity<>(createdMenuItem, HttpStatus.CREATED);
        }
    }

    /**
     * PUT /api/menu-items/{id}
     * Updates an existing menu item.
     * Access Control: ADMIN or CAFETERIA_OWNER (for menu items in their own cafeteria).
     *
     * @param id The UUID of the menu item to update.
     * @param menuItemDetails The MenuItem object with updated details (sent in request body).
     * @return ResponseEntity with the updated MenuItem object and HTTP status 200 OK,
     * or 404 Not Found if the menu item does not exist, or 403 Forbidden if not authorized.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CAFETERIA_OWNER') and @menuItemService.getMenuItemById(#id).orElse(null)?.cafeteria?.cafeteriaId == @userService.getCurrentUserCafeteriaId())")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable UUID id, @RequestBody MenuItem menuItemDetails) {
        MenuItem updatedMenuItem = menuItemService.updateMenuItem(id, menuItemDetails);
        if (updatedMenuItem != null) {
            return ResponseEntity.ok(updatedMenuItem);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/menu-items/{id}
     * Deletes a menu item by its ID.
     * Access Control: ADMIN or CAFETERIA_OWNER (for menu items in their own cafeteria).
     *
     * @param id The UUID of the menu item to delete.
     * @return ResponseEntity with HTTP status 204 No Content if deleted,
     * or 404 Not Found if the item does not exist, or 403 Forbidden if not authorized.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CAFETERIA_OWNER') and @menuItemService.getMenuItemById(#id).orElse(null)?.cafeteria?.cafeteriaId == @userService.getCurrentUserCafeteriaId())")
    public ResponseEntity<HttpStatus> deleteMenuItem(@PathVariable UUID id) {
        boolean deleted = menuItemService.deleteMenuItem(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
