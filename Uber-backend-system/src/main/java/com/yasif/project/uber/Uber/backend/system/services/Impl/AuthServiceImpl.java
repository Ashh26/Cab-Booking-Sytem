package com.yasif.project.uber.Uber.backend.system.services.Impl;

import com.yasif.project.uber.Uber.backend.system.dto.DriverDto;
import com.yasif.project.uber.Uber.backend.system.dto.SignupDto;
import com.yasif.project.uber.Uber.backend.system.dto.UserDto;
import com.yasif.project.uber.Uber.backend.system.entities.Driver;
import com.yasif.project.uber.Uber.backend.system.entities.User;
import com.yasif.project.uber.Uber.backend.system.entities.enums.Role;
import com.yasif.project.uber.Uber.backend.system.exceptions.ResourceNotFoundException;
import com.yasif.project.uber.Uber.backend.system.exceptions.RuntimeConflictException;
import com.yasif.project.uber.Uber.backend.system.repositories.UserRepository;
import com.yasif.project.uber.Uber.backend.system.security.JWTService;
import com.yasif.project.uber.Uber.backend.system.services.AuthService;
import com.yasif.project.uber.Uber.backend.system.services.DriverService;
import com.yasif.project.uber.Uber.backend.system.services.RiderService;
import com.yasif.project.uber.Uber.backend.system.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.yasif.project.uber.Uber.backend.system.entities.enums.Role.DRIVER;

@RequiredArgsConstructor
// Generates constructor for all final fields.
// Enables clean constructor-based dependency injection.
@Service
@Transactional
// Ensures all public methods run inside a transaction.
// Any failure will roll back database changes.
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    // Handles database operations related to User entity.

    private final ModelMapper modelMapper;
    // Used to convert between Entity and DTO objects.

    private final RiderService riderService;
    // Manages rider-specific domain logic.

    private final WalletService walletService;
    // Responsible for wallet creation and management.

    private final DriverService driverService;
    // Handles driver-related operations.

    private final PasswordEncoder passwordEncoder;
    // Encodes user passwords before persisting them.

    private final AuthenticationManager authenticationManager;
    // Delegates authentication to Spring Security.

    private final JWTService jwtService;
    // Generates and validates JWT access and refresh tokens.


    @Override
    public String[] login(String email, String password) {

        // Authenticates user credentials using Spring Security.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // Retrieves the authenticated user principal.
        User user = (User) authentication.getPrincipal();

        // Generates short-lived access token.
        String accessToken = jwtService.generateAccessToken(user);

        // Generates long-lived refresh token.
        String refreshToken = jwtService.generateRefreshToken(user);

        // Returns both tokens as an array.
        return new String[]{accessToken, refreshToken};
    }


    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto) {

        // Checks if a user already exists with the given email.
        User user = userRepository
                .findByEmail(signupDto.getEmail())
                .orElse(null);

        // Prevents duplicate user registration.
        if (user != null) {
            throw new RuntimeConflictException(
                    "User already exist with email " + signupDto.getEmail()
            );
        }

        // Maps signup request DTO to User entity.
        User mapUser = modelMapper.map(signupDto, User.class);

        // Assigns default role as RIDER.
        mapUser.setRoles(Set.of(Role.RIDER));

        // Encodes the password before saving to database.
        mapUser.setPassword(
                passwordEncoder.encode(mapUser.getPassword())
        );

        // Persists the user entity.
        User savedUser = userRepository.save(mapUser);

        // Creates a rider profile for the new user.
        riderService.createNewRider(savedUser);

        // Initializes wallet for the newly registered user.
        walletService.createNewWallet(savedUser);

        // Returns user data as DTO.
        return modelMapper.map(savedUser, UserDto.class);
    }


    @Override
    public DriverDto onBoardNewDriver(Long userId, String vehicleId) {

        // Fetches user by ID.
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "User not found with id:" + userId
                )
        );

        // Checks if user is already a driver.
        if (user.getRoles().contains(DRIVER)) {
            throw new RuntimeConflictException(
                    "User with id:" + userId + " already exists"
            );
        }

        // Builds a new Driver entity.
        Driver createDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .vehicleId(vehicleId)
                .available(true)
                .build();

        // Adds DRIVER role to the user.
        user.getRoles().add(DRIVER);

        // Saves updated user with new role.
        userRepository.save(user);

        // Persists the driver entity.
        Driver savedDriver =
                driverService.createNewDriver(createDriver);

        // Converts Driver entity to DTO and returns.
        return modelMapper.map(savedDriver, DriverDto.class);
    }


    @Override
    public String refreshToken(String refreshToken) {

        // Extracts userId from refresh token.
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        // Fetches user associated with the token.
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "User not found with id:" + userId
                )
        );

        // Generates and returns a new access token.
        return jwtService.generateAccessToken(user);
    }
}
