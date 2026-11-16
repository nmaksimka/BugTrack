package io.github.nmaksimka.bugtrack.project;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.user.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "memberships")
public class Membership extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "membership_roles", joinColumns = @JoinColumn(name = "membership_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<ProjectRole> roles = EnumSet.noneOf(ProjectRole.class);

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Membership() {
    }

    Membership(User user, Project project, Set<ProjectRole> roles) {
        this.user = user;
        this.project = project;
        this.active = true;
        this.createdAt = Instant.now();
        changeRoles(roles);
    }

    public void changeRoles(Set<ProjectRole> newRoles) {
        if (newRoles == null || newRoles.isEmpty()) {
            throw new DomainException("У участника должна быть хотя бы одна роль");
        }
        roles.clear();
        roles.addAll(newRoles);
    }

    public void disable() {
        active = false;
    }

    void activate(Set<ProjectRole> newRoles) {
        changeRoles(newRoles);
        active = true;
    }

    public boolean has(ProjectRole role) {
        return active && roles.contains(role);
    }

    public boolean hasAny(ProjectRole... candidates) {
        for (ProjectRole role : candidates) {
            if (has(role)) {
                return true;
            }
        }
        return false;
    }

    public User getUser() {
        return user;
    }

    public Project getProject() {
        return project;
    }

    public Set<ProjectRole> getRoles() {
        return Set.copyOf(roles);
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
