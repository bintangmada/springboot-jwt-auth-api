package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.dto.user.UpdateUserRequest;
import com.bintang.jwt.auth.dto.user.UserResponse;
import com.bintang.jwt.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable("id") Long id, @RequestBody @Valid UpdateUserRequest request){
        return ResponseEntity.ok(userService.update(id, request));
    }


}
