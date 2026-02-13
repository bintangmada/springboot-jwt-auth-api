package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.repository.PermissionRepository;
import com.bintang.jwt.auth.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;


}
