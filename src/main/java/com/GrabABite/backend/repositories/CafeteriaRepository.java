package com.grababite.backend.repositories;

import com.grababite.backend.models.Cafeteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CafeteriaRepository extends JpaRepository<Cafeteria, UUID> {
}