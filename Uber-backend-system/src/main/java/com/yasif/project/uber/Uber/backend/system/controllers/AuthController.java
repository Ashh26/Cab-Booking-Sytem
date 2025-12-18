package com.yasif.project.uber.Uber.backend.system.controllers;

import com.yasif.project.uber.Uber.backend.system.dto.*;
import com.yasif.project.uber.Uber.backend.system.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    ResponseEntity<UserDto> singUp(@RequestBody SignupDto signupDto){
        return new ResponseEntity<>(authService.signup(signupDto), HttpStatus.CREATED);
    }

    @Secured("ROLE_ADMIN")
// Restricts this API to ADMIN users only.
// If a non-admin user calls this API, access will be denied.

    @PostMapping("onBoardNewDriver/{userId}")
// Maps this method to a POST request.
// userId is taken from the URL path.

    public ResponseEntity<DriverDto> onBoardNewDriver(
            @PathVariable Long userId,
            // Extracts userId from the URL.

            @RequestBody OnboardDriverDto onboardDriverDto
            // Reads driver onboarding details from request body (e.g. vehicleId).
    ) {

        // Calls service layer to onboard a new driver
        // and assign a vehicle to the user.
        return new ResponseEntity<>(
                authService.onBoardNewDriver(
                        userId,
                        onboardDriverDto.getVehicleId()
                ),
                // Returns HTTP 201 status indicating resource creation.
                HttpStatus.CREATED
        );
    }


    @PostMapping("/login")
// Maps this method to the login API endpoint.

    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto,
            // Reads login credentials (email & password) from request body.

            HttpServletRequest httpServletRequest,
            // Provides access to incoming HTTP request data.

            HttpServletResponse httpServletResponse
            // Used to add cookies to the HTTP response.
    ) {

        // Authenticates user and generates tokens.
        // tokens[0] -> access token
        // tokens[1] -> refresh token
        String[] tokens = authService.login(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );

        // Creates a cookie to store the refresh token.
        Cookie cookie = new Cookie("refreshToken", tokens[1]);

        // Makes the cookie inaccessible to JavaScript for security.
        cookie.setHttpOnly(true);

        // Adds the refresh token cookie to the response.
        httpServletResponse.addCookie(cookie);

        // Returns access token in response body with HTTP 200 status.
        return ResponseEntity.ok(
                new LoginResponseDto(tokens[0])
        );
    }


    @PostMapping("/refresh")
// Endpoint used to generate a new access token using refresh token.

    public ResponseEntity<LoginResponseDto> refresh(
            HttpServletRequest request
            // Used to read cookies from the incoming request.
    ) {

        // Extracts refresh token from cookies.
        String refreshToken = Arrays.stream(request.getCookies())
                // Iterates through all cookies.

                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                // Finds the cookie named "refreshToken".

                .findFirst()
                // Gets the first matching cookie.

                .map(Cookie::getValue)
                // Extracts the refresh token value.

                .orElseThrow(() ->
                        // Throws error if refresh token is missing.
                        new AuthenticationServiceException(
                                "Refresh token not found inside the Cookie"
                        )
                );

        // Generates a new access token using the refresh token.
        String accessToken = authService.refreshToken(refreshToken);

        // Returns the new access token with HTTP 200 status.
        return ResponseEntity.ok(
                new LoginResponseDto(accessToken)
        );
    }


}







