package com.example.lmchecker.controller;

import com.example.lmchecker.dto.ApiResponse;
import com.example.lmchecker.dto.UserDto;
import com.example.lmchecker.entity.Role;
import com.example.lmchecker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("User retrieved", userService.getUserById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String email = payload.get("email");
        String password = payload.get("password");
        String fullName = payload.get("fullName");
        Role role = Role.valueOf(payload.getOrDefault("role", "REGULATOR"));

        UserDto created = userService.createUser(username, email, password, fullName, role);
        return ResponseEntity.ok(ApiResponse.ok("User created successfully", created));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> toggleStatus(@PathVariable Long id) {
        UserDto updated = userService.toggleUserEnabled(id);
        return ResponseEntity.ok(ApiResponse.ok("User status updated", updated));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateRole(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String roleStr = payload.get("role");
        if (roleStr == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Role is required"));
        }
        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid role: " + roleStr));
        }
        UserDto updated = userService.updateUserRole(id, role);
        return ResponseEntity.ok(ApiResponse.ok("User role updated successfully", updated));
    }

    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> unlockUser(@PathVariable Long id) {
        UserDto updated = userService.unlockUserAccount(id);
        return ResponseEntity.ok(ApiResponse.ok("User account unlocked successfully", updated));
    }
}
