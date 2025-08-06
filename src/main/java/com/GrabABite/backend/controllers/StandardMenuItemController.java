package com.grababite.backend.controllers;

import com.grababite.backend.models.StandardMenuItem;
import com.grababite.backend.services.StandardMenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // Import for Collectors

@RestController
@RequestMapping("/api/standard-menu-items") // Base URL for standard menu items
public class StandardMenuItemController {

    @Autowired
    private StandardMenuItemService standardMenuItemService;

    /**
     * GET /api/standard-menu-items
     * Retrieves a list of all standard menu items.
     * @return A list of StandardMenuItem objects.
     */
    @GetMapping
    public List<StandardMenuItem> getAllStandardMenuItems() {
        return standardMenuItemService.getAllStandardMenuItems();
    }

    /**
     * GET /api/standard-menu-items/{id}
     * Retrieves a single standard menu item by its ID.
     * @param id The UUID of the standard menu item to retrieve.
     * @return ResponseEntity with the StandardMenuItem object and HTTP status 200 OK,
     * or 404 Not Found if the item does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardMenuItem> getStandardMenuItemById(@PathVariable UUID id) {
        return standardMenuItemService.getStandardMenuItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/standard-menu-items
     * Creates a new standard menu item.
     * @param standardMenuItem The StandardMenuItem object to create (sent in the request body as JSON).
     * @return ResponseEntity with the created StandardMenuItem object and HTTP status 201 Created.
     */
    @PostMapping
    public ResponseEntity<StandardMenuItem> createStandardMenuItem(@RequestBody StandardMenuItem standardMenuItem) {
        StandardMenuItem createdItem = standardMenuItemService.createStandardMenuItem(standardMenuItem);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    /**
     * POST /api/standard-menu-items/bulk
     * Creates multiple new standard menu items in a single request.
     * @param standardMenuItems A list of StandardMenuItem objects to create (sent in the request body as JSON array).
     * @return ResponseEntity with the list of created StandardMenuItem objects and HTTP status 201 Created.
     */
    @PostMapping("/bulk") // New endpoint for bulk creation
    public ResponseEntity<List<StandardMenuItem>> createStandardMenuItemsBulk(@RequestBody List<StandardMenuItem> standardMenuItems) {
        List<StandardMenuItem> createdItems = standardMenuItems.stream()
                                                .map(standardMenuItemService::createStandardMenuItem)
                                                .collect(Collectors.toList());
        return new ResponseEntity<>(createdItems, HttpStatus.CREATED);
    }

    /**
     * PUT /api/standard-menu-items/{id}
     * Updates an existing standard menu item.
     * @param id The UUID of the standard menu item to update.
     * @param standardMenuItemDetails The StandardMenuItem object with updated details (sent in request body).
     * @return ResponseEntity with the updated StandardMenuItem object and HTTP status 200 OK,
     * or 404 Not Found if the item does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardMenuItem> updateStandardMenuItem(@PathVariable UUID id, @RequestBody StandardMenuItem standardMenuItemDetails) {
        StandardMenuItem updatedItem = standardMenuItemService.updateStandardMenuItem(id, standardMenuItemDetails);
        if (updatedItem != null) {
            return ResponseEntity.ok(updatedItem);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/standard-menu-items/{id}
     * Deletes a standard menu item by its ID.
     * @param id The UUID of the standard menu item to delete.
     * @return ResponseEntity with HTTP status 204 No Content if deleted,
     * or 404 Not Found if the item does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteStandardMenuItem(@PathVariable UUID id) {
        boolean deleted = standardMenuItemService.deleteStandardMenuItem(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
