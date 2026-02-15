package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @PostMapping("/user-role")
    public void assignRoleToUser(@RequestParam Long userId, @RequestParam Long roleId) {
        accessService.assignRoleToUser(userId, roleId);
    }
}
