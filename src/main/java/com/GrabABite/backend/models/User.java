package com.grababite.backend.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore; // Import JsonIgnore

@Entity
@Table(name = "users")
public class User extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "auth_id", unique = true) // authId can be null if user is registered via username/password
    private String authId;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // NEW: Password field for username/password authentication
    @Column(name = "password") // Can be null if using authId (external provider)
    private String password;

    // NEW: Roles as a collection for multiple roles
    @ElementCollection(fetch = FetchType.EAGER) // Fetch roles eagerly when user is loaded
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name") // Name of the column in the user_roles table
    private Set<String> roles = new HashSet<>(); // Use Set to avoid duplicate roles

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    @JsonIgnore // NEW: Ignore during JSON serialization to prevent proxy issues
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_id")
    @JsonIgnore // NEW: Ignore during JSON serialization to prevent proxy issues
    private Cafeteria cafeteria;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // NEW: Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // NEW: Getter and Setter for roles (plural)
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    // Helper method to add a single role
    public void addRole(String role) {
        this.roles.add(role);
    }

    // IMPORTANT: If you need college/cafeteria details in the response,
    // you'll need to either eagerly fetch them in a specific query (e.g., using JOIN FETCH),
    // or use DTOs to manually map relevant fields. For now, they are ignored in the direct User entity response.
    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }

    public Cafeteria getCafeteria() {
        return cafeteria;
    }

    public void setCafeteria(Cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }
}
