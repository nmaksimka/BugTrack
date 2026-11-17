package io.github.nmaksimka.bugtrack.security;

import io.github.nmaksimka.bugtrack.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record CurrentUser(UUID id, String name, String email, String passwordHash, boolean active, boolean admin)
        implements UserDetails {

    public static CurrentUser from(User user) {
        return new CurrentUser(user.getId(), user.getName(), user.getEmail(),
                user.getPasswordHash(), user.isActive(), user.isAdmin());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return admin
                ? List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
