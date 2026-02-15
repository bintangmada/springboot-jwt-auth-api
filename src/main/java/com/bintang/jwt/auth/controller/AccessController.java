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
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user-role")
    public ResponseEntity<Void> revokeRoleFromUser(@RequestParam Long userId, @RequestParam Long roleId) {
        accessService.revokeRoleFromUser(userId, roleId);
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/role-permission")
    public ResponseEntity<Void> assignPermissionToRole(@RequestParam Long roleId, @RequestParam Long permissionId) {
        accessService.assignPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/role-permission")
    public ResponseEntity<Void> revokePermissionFromRole(@RequestParam Long roleId, @RequestParam Long permissionId) {
        accessService.revokePermissionFromRole(roleId, permissionId);
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/user-permission")
    public ResponseEntity<Void> assignPermissionToUser(@RequestParam Long userId, @RequestParam Long permissionId) {
        accessService.assignPermissionToUser(userId, permissionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user-permission")
    public ResponseEntity<Void> revokePermissionFromUser(@RequestParam Long userId, @RequestParam Long permissionId) {
        accessService.revokePermissionFromUser(userId, permissionId);
        return ResponseEntity.notFound().build();
    }

}
