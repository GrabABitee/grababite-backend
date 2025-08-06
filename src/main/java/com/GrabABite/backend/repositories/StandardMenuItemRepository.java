package com.grababite.backend.repositories;

import com.grababite.backend.models.StandardMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StandardMenuItemRepository extends JpaRepository<StandardMenuItem, UUID> {
    // Custom query to find a standard menu item by its name
    Optional<StandardMenuItem> findByName(String name);
}
