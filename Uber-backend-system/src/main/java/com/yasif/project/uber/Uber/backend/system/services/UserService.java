package com.yasif.project.uber.Uber.backend.system.services;

import com.yasif.project.uber.Uber.backend.system.entities.User;
import com.yasif.project.uber.Uber.backend.system.exceptions.ResourceNotFoundException;
import com.yasif.project.uber.Uber.backend.system.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    // Repository to perform CRUD operations on User entity.

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Called by Spring Security during authentication.
        // Finds user by email (used as username in this application).
        // Returns User object if found, otherwise null.
        return userRepository.findByEmail(username).orElse(null);
    }

    public User getUserById(Long id) {

        // Retrieves a User by ID from the database.
        // Throws ResourceNotFoundException if no user exists with the given ID.
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "User not found with id:" + id
                )
        );
    }

}

