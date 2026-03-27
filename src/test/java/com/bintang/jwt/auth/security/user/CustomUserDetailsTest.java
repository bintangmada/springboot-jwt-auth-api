package com.bintang.jwt.auth.security.user;

import com.bintang.jwt.auth.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void testUserDetailsMethods() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@ex.com");
        user.setPassword("secret");

        CustomUserDetails userDetails = new CustomUserDetails(
                user,
                Set.of("ADMIN"),
                Set.of("USER_READ")
        );

        assertEquals("test@ex.com", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        assertEquals(1L, userDetails.getUserId());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("USER_READ")));

        // Boilerplate truth methods
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testUserDetailsMethods_WithNullPermissions() {
        User user = new User();
        CustomUserDetails userDetails = new CustomUserDetails(
                user,
                Set.of("USER"),
                Set.of()
        );

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
