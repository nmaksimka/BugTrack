package io.github.nmaksimka.bugtrack.user;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_role", nullable = false)
    private SystemRole systemRole;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected User() {
    }

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = normalize(email);
        this.passwordHash = passwordHash;
        this.systemRole = SystemRole.USER;
        this.active = true;
        this.createdAt = Instant.now();
    }

    public static String normalize(String email) {
        return email.trim().toLowerCase();
    }

    public void block() {
        active = false;
    }

    public void unblock() {
        active = true;
    }

    public void changePassword(String newPasswordHash) {
        passwordHash = newPasswordHash;
    }

    public void makeAdmin() {
        systemRole = SystemRole.ADMIN;
    }

    public boolean isAdmin() {
        return systemRole == SystemRole.ADMIN;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public SystemRole getSystemRole() {
        return systemRole;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
