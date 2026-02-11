package com.bintang.jwt.auth.entity;

import com.bintang.jwt.auth.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserPermission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long permissionId;

}
