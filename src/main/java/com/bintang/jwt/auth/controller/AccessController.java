package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @PostMapping("/user-role")
    public ResponseEntity<Void> assignRoleToUser(@RequestParam Long userId, @RequestParam Long roleId) {
        accessService.assignRoleToUser(userId, roleId);
        return ResponseEntity.notFound().build();
    }
}
