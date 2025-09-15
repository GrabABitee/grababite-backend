package com.grababite.backend.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grababite.backend.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email); 
    Optional<User> findByAuthId(String authId);

    // ✅ Check if any user has a given role (e.g., "ADMIN")
    @Query("select (count(u) > 0) from User u join u.roles r where r = :role")
    boolean existsByRole(@Param("role") String role);
}
