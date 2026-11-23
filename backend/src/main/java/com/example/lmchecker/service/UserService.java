package com.example.lmchecker.service;

import com.example.lmchecker.dto.UserDto;
import com.example.lmchecker.entity.Role;
import com.example.lmchecker.entity.User;
import com.example.lmchecker.exception.BadRequestException;
import com.example.lmchecker.exception.ResourceNotFoundException;
import com.example.lmchecker.mapper.UserMapper;
import com.example.lmchecker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public UserDto createUser(String username, String email, String password, String fullName, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already in use");
        }

        User user = User.builder()
            .username(username)
            .email(email)
            .passwordHash(passwordEncoder.encode(password))
            .fullName(fullName)
            .role(role)
            .enabled(true)
            .emailVerified(true)
            .accountStatus("ACTIVE")
            .failedLoginAttempts(0)
            .build();

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto toggleUserEnabled(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(!user.isEnabled());
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto updateUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setRole(newRole);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto unlockUserAccount(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        user.setAccountStatus("ACTIVE");
        return userMapper.toDto(userRepository.save(user));
    }
}
