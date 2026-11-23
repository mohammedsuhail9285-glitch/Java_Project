package com.example.lmchecker.mapper;

import com.example.lmchecker.dto.UserDto;
import com.example.lmchecker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole(),
            user.getAuthProvider() != null ? user.getAuthProvider().name() : "LOCAL",
            user.getPhoneNumber(),
            user.isEmailVerified(),
            user.getAccountStatus() != null ? user.getAccountStatus() : "ACTIVE",
            user.isEnabled(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
