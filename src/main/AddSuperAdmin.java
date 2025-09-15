package com.grababite.backend;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.util.Optional;
import java.time.LocalDateTime;

/**
 * This class is a command-line runner that adds a new user with ADMIN role.
 * It is a safer approach than exposing an API endpoint for creating admin users.
 *
 * To run this, place this file in your project's source directory (e.g., src/main/java/com/yourcompany/yourapp).
 * You will also need to have the following dependencies in your pom.xml or build.gradle:
 * - spring-boot-starter-data-jpa
 * - spring-boot-starter-web
 * - spring-boot-starter-security (for PasswordEncoder)
 *
 * Make sure your application.properties is configured with your database settings.
 * After running, the new user will be created and the application will shut down.
 */
@SpringBootApplication
@EnableJpaRepositories
public class AddSuperAdmin {

    public static void main(String[] args) {
        SpringApplication.run(AddSuperAdmin.class, args);
    }

    /**
     * Creates a BCryptPasswordEncoder bean for password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This inner class defines the User entity, matching the 'users' table in your database.
     * Ensure the column names match your database schema.
     */
    @Entity
    @Table(name = "users")
    public static class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String password;

        private String name;
        private String authId;
        private Long collegeId;
        private Long cafeteriaId;

        @Column(name = "created_at")
        private LocalDateTime createdAt;
        
        // Enum or string for role, based on your implementation
        private String role; 

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        // Note: For brevity, other getters/setters are omitted but should be included.
    }

    /**
     * JPA Repository interface for the User entity. Spring Data JPA will
     * automatically create the implementation for this.
     */
    public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);
    }

    /**
     * This component runs when the application starts, performing the user creation logic.
     */
    @Component
    public static class AdminUserCreator implements CommandLineRunner {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public AdminUserCreator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) throws Exception {
            final String adminEmail = "admin@example.com";
            final String adminPassword = "test@123";
            final String adminName = "Super Admin User";

            // Check if the admin user already exists to prevent duplicates
            Optional<User> existingUser = userRepository.findByEmail(adminEmail);
            if (existingUser.isPresent()) {
                System.out.println("User '" + adminEmail + "' already exists. Skipping creation.");
                return;
            }

            // Create and save the new user
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setName(adminName);
            admin.setRole("ADMIN"); // Use the role string or enum value for super admin
            admin.setCreatedAt(LocalDateTime.now());
            
            userRepository.save(admin);

            System.out.println("Successfully created new super admin user:");
            System.out.println("Email: " + adminEmail);
            System.out.println("Name: " + adminName);
            System.out.println("Role: ADMIN");
        }
    }
}
