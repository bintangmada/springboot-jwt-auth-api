package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.role.RoleRequest;
import com.bintang.jwt.auth.dto.role.RoleResponse;
import com.bintang.jwt.auth.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody @Valid RoleRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll(){
        return ResponseEntity.ok().body(roleService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoleRequest request)
    {
        return ResponseEntity.ok().body(roleService.update(id, request));
    }

}
