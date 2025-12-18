package com.yasif.project.uber.Uber.backend.system.entities;

import com.yasif.project.uber.Uber.backend.system.entities.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
// Marks this class as a JPA entity to be persisted in the database.

@Table(name = "uber_user", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
})
// Specifies the table name in the database as "uber_user".
// Creates a database index on the "email" column to improve
// query performance for lookups by email.

@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
// Indicates that roles are stored as a collection in a separate table.
// EAGER fetch ensures roles are loaded immediately with the user entity
// because Spring Security needs them during authentication.
    @Enumerated(EnumType.STRING)
// Stores enum values as readable strings in the database
// (e.g. ADMIN instead of 0 or 1).

    private Set<Role> roles;
// Holds all roles assigned to the user (ADMIN, USER, DRIVER, etc.).


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Converts application roles into Spring Security authorities.
        // Spring Security works with GrantedAuthority, not raw enums.
        return roles.stream()

                // Prefixes each role with "ROLE_"
                // This is mandatory for Spring Security role checks.
                .map(role ->
                        new SimpleGrantedAuthority("ROLE_" + role.name())
                )

                // Collects all authorities into a Set.
                .collect(Collectors.toSet());
    }


    @Override
    public String getUsername() {

        // Defines what Spring Security treats as the username.
        // Here, email is used instead of a separate username field.
        return email;
    }
    }

