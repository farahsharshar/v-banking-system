package com.vbank.user.controller;

import com.vbank.user.dto.UserRegistrationDto;
import com.vbank.user.dto.UserLoginDto;
import com.vbank.user.dto.UserResponseDto;
import com.vbank.user.service.UserService;
import com.vbank.user.kafka.LoggingProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoggingProducer loggingProducer;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto, HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest(registrationDto.toString(), "POST /users/register");

        UserResponseDto response = userService.registerUser(registrationDto);

        // Log response
        loggingProducer.logResponse(response.toString(), "POST /users/register");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> loginUser(@Valid @RequestBody UserLoginDto loginDto, HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest(loginDto.toString(), "POST /users/login");

        UserResponseDto response = userService.loginUser(loginDto);

        // Log response
        loggingProducer.logResponse(response.toString(), "POST /users/login");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable UUID userId, HttpServletRequest request) {
        // Log request
        loggingProducer.logRequest("GET /users/" + userId + "/profile", "GET /users/{userId}/profile");

        UserResponseDto response = userService.getUserProfile(userId);

        // Log response
        loggingProducer.logResponse(response.toString(), "GET /users/{userId}/profile");

        return ResponseEntity.ok(response);
    }
}
