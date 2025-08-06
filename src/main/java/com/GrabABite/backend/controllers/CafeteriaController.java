package com.grababite.backend.controllers;

import com.grababite.backend.dto.CafeteriaCreationRequest; // Import the new DTO
import com.grababite.backend.models.Cafeteria;
import com.grababite.backend.models.College;
import com.grababite.backend.services.CafeteriaService;
import com.grababite.backend.services.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/cafeterias")
public class CafeteriaController {

    @Autowired
    private CafeteriaService cafeteriaService;

    @Autowired
    private CollegeService collegeService;

    @GetMapping
    public List<Cafeteria> getAllCafeterias() {
        return cafeteriaService.getAllCafeterias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cafeteria> getCafeteriaById(@PathVariable UUID id) {
        return cafeteriaService.getCafeteriaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/cafeterias
     * Creates a new cafeteria.
     * The request body now includes all cafeteria details and the 'collegeId'.
     * @param request The CafeteriaCreationRequest DTO containing cafeteria details and collegeId.
     * @return ResponseEntity with the created Cafeteria object and HTTP status 201 Created,
     * or 400 Bad Request if college not found.
     */
    @PostMapping
    public ResponseEntity<Cafeteria> createCafeteria(@RequestBody CafeteriaCreationRequest request) {
        // Find the College by its ID
        Optional<College> collegeOptional = collegeService.getCollegeById(request.getCollegeId());

        if (collegeOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // College not found
        }

        // Create a Cafeteria entity from the DTO
        Cafeteria cafeteria = new Cafeteria();
        cafeteria.setName(request.getName());
        cafeteria.setLocation(request.getLocation());
        cafeteria.setIsOpen(request.getIsOpen());
        cafeteria.setCollege(collegeOptional.get()); // Set the associated College object

        Cafeteria createdCafeteria = cafeteriaService.createCafeteria(cafeteria);
        return new ResponseEntity<>(createdCafeteria, HttpStatus.CREATED);
    }

    /**
     * PUT /api/cafeterias/{id}
     * Updates an existing cafeteria.
     * @param id The UUID of the cafeteria to update.
     * @param cafeteriaDetails The Cafeteria object with updated details (sent in request body).
     * @param collegeId (Optional) The UUID of the new college this cafeteria should belong to.
     * @return ResponseEntity with the updated Cafeteria object and HTTP status 200 OK,
     * or 404 Not Found if the cafeteria does not exist, or 400 Bad Request if collegeId is invalid.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cafeteria> updateCafeteria(@PathVariable UUID id, @RequestBody Cafeteria cafeteriaDetails, @RequestParam(required = false) UUID collegeId) {
        if (collegeId != null) {
            Optional<College> collegeOptional = collegeService.getCollegeById(collegeId);
            if (collegeOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // New College for update not found
            }
            cafeteriaDetails.setCollege(collegeOptional.get());
        }
        Cafeteria updatedCafeteria = cafeteriaService.updateCafeteria(id, cafeteriaDetails);
        if (updatedCafeteria != null) {
            return ResponseEntity.ok(updatedCafeteria);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/cafeterias/{id}
     * Deletes a cafeteria by its ID.
     * @param id The UUID of the cafeteria to delete.
     * @return ResponseEntity with HTTP status 204 No Content if deleted,
     * or 404 Not Found if the cafeteria does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCafeteria(@PathVariable UUID id) {
        boolean deleted = cafeteriaService.deleteCafeteria(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
