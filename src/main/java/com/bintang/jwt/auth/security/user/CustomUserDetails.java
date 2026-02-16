package com.bintang.jwt.auth.security.user;

import com.bintang.jwt.auth.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final boolean active;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(
            User user,
            Set<String> roleNames,
            Set<String> permissionNames) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.active = true;

        Set<GrantedAuthority> auths = new HashSet<>();

        roleNames.forEach(r -> auths.add(new SimpleGrantedAuthority("ROLE_" + r)));
        permissionNames.forEach(p -> auths.add(new SimpleGrantedAuthority(p)));

        this.authorities = Collections.unmodifiableSet(auths);
    }

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.active = true;
        this.authorities = new HashSet<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

}
