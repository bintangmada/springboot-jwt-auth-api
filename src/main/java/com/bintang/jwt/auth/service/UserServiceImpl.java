package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.dto.user.UpdateUserRequest;
import com.bintang.jwt.auth.dto.user.UserResponse;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.entity.enums.AuthProvider;
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

    public UserResponse createUser(RegisterRequest request){

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("User is already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .providerId(AuthProvider.LOCAL.toString())
                .build();

        return toResponse(userRepository.save(user));
    }

    public UserResponse getById(Long id){
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User is not found"));
    }

    public List<UserResponse> getAll(){
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse update(Long id, UpdateUserRequest request){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User is not found"));

        user.setName(request.getName());
        
        return toResponse(userRepository.save(user));

    }

    @Transactional
    @Override
    public void delete(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        user.setStatus(0L);
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(auth != null ? auth.getName() : "SYSTEM");

        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .provider(user.getProviderId())
                .build();
    }

}
