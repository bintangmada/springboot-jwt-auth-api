package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.dto.user.UpdateUserRequest;
import com.bintang.jwt.auth.dto.user.UserResponse;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.entity.enums.AuthProvider;
import com.bintang.jwt.auth.exception.ConflictException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    public UserResponse create(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("User already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authProvider("LOCAL")
                .providerId(null)
                .status(1L)
                .isDeleted(false)
                .build();

        return toResponse(userRepository.save(user));
    }

    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse update(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getEmail() != null
                && !request.getEmail().equals(user.getEmail())
                && userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new ConflictException("Email already used");
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        return toResponse(userRepository.save(user));

    }

    @Transactional
    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        user.setStatus(0L);
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(currentUser());

        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getAuthProvider())
                .build();
    }

}
