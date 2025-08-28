package com.vbank.user.service;

import com.vbank.user.dto.UserRegistrationDto;
import com.vbank.user.dto.UserLoginDto;
import com.vbank.user.dto.UserResponseDto;
import com.vbank.user.model.User;
import com.vbank.user.repository.UserRepository;
import com.vbank.user.exception.UserAlreadyExistsException;
import com.vbank.user.exception.InvalidCredentialsException;
import com.vbank.user.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.UUID;
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists.");
        }

        // Create new user with hashed password
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());

        User savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getId(), savedUser.getUsername(), "User registered successfully.");
    }

    public UserResponseDto loginUser(UserLoginDto loginDto) {
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        return new UserResponseDto(user.getId(), user.getUsername(), "Login successful.");
    }

    public UserResponseDto getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found."));

        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
