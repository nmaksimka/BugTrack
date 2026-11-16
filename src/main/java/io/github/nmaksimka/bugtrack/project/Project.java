package io.github.nmaksimka.bugtrack.project;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "project_key", nullable = false, unique = true)
    private String key;

    private String description;

    private String repository;

    private LocalDate deadline;

    @Column(name = "bug_sequence", nullable = false)
    private int bugSequence;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Membership> memberships = new ArrayList<>();

    protected Project() {
    }

    public Project(String name, String key, String description, String repository, LocalDate deadline) {
        this.name = name.trim();
        this.key = key.trim().toUpperCase();
        this.description = description;
        this.repository = repository;
        this.deadline = deadline;
        this.bugSequence = 0;
        this.createdAt = Instant.now();
    }

    public Membership addMember(User user, Set<ProjectRole> roles) {
        Optional<Membership> existing = findMembership(user);
        if (existing.isPresent()) {
            Membership membership = existing.get();
            if (membership.isActive()) {
                throw new DomainException("Пользователь уже участвует в проекте");
            }
            membership.activate(roles);
            return membership;
        }
        Membership membership = new Membership(user, this, roles);
        memberships.add(membership);
        return membership;
    }

    public void changeDeadline(LocalDate newDeadline) {
        deadline = newDeadline;
    }

    public void update(String name, String description, String repository) {
        this.name = name.trim();
        this.description = description;
        this.repository = repository;
    }

    public int nextBugNumber() {
        bugSequence++;
        return bugSequence;
    }

    public Optional<Membership> findMembership(User user) {
        return memberships.stream()
                .filter(m -> m.getUser().getId().equals(user.getId()))
                .findFirst();
    }

    public List<Membership> getActiveMemberships() {
        return memberships.stream().filter(Membership::isActive).toList();
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getRepository() {
        return repository;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Membership> getMemberships() {
        return List.copyOf(memberships);
    }
}
