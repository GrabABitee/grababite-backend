package com.grababite.backend.repositories;

import com.grababite.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email); // Existing method, good for login
    Optional<User> findByAuthId(String authId); // Existing method for external auth
}
