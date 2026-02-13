package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.permission.PermissionRequest;
import com.bintang.jwt.auth.dto.permission.PermissionResponse;
import com.bintang.jwt.auth.entity.Permission;
import com.bintang.jwt.auth.repository.PermissionRepository;
import com.bintang.jwt.auth.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> update(@PathVariable("id") Long id, @RequestBody @Valid PermissionRequest request) {
        return ResponseEntity.ok().body(permissionService.update(id, request));
    }

}
