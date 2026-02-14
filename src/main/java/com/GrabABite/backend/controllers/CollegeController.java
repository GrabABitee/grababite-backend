package com.grababite.backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grababite.backend.models.Cafeteria;
import com.grababite.backend.models.College;
import com.grababite.backend.repositories.CafeteriaRepository;
import com.grababite.backend.services.CollegeService;

@RestController
@RequestMapping("/api/colleges")
public class CollegeController {

    @Autowired
    private CollegeService collegeService;

    @Autowired
    private CafeteriaRepository cafeteriaRepository;

    // Get all colleges
    @GetMapping
    public List<College> getAllColleges() {
        return collegeService.getAllColleges();
    }

    // Get a college by ID
    @GetMapping("/{id}")
    public ResponseEntity<College> getCollegeById(@PathVariable UUID id) {
        return collegeService.getCollegeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new college
    @PostMapping
    public ResponseEntity<College> createCollege(@RequestBody College college) {
        College createdCollege = collegeService.createCollege(college);
        return new ResponseEntity<>(createdCollege, HttpStatus.CREATED);
    }

    // Update a college
    @PutMapping("/{id}")
    public ResponseEntity<College> updateCollege(@PathVariable UUID id, @RequestBody College collegeDetails) {
        College updatedCollege = collegeService.updateCollege(id, collegeDetails);
        if (updatedCollege != null) {
            return ResponseEntity.ok(updatedCollege);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete a college
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCollege(@PathVariable UUID id) {
        boolean deleted = collegeService.deleteCollege(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ✅ Get cafeterias by college
    @GetMapping("/{collegeId}/cafeterias")
    public List<Cafeteria> getCafeteriasByCollege(@PathVariable UUID collegeId) {
        return cafeteriaRepository.findByCollege_CollegeId(collegeId);
    }
}
