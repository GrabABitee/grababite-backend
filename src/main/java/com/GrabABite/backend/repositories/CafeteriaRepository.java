package com.grababite.backend.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grababite.backend.models.Cafeteria;

@Repository
public interface CafeteriaRepository extends JpaRepository<Cafeteria, UUID> {
    // ✅ Custom query to fetch cafeterias by collegeId
    List<Cafeteria> findByCollege_CollegeId(UUID collegeId);
}
