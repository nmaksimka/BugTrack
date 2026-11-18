package io.github.nmaksimka.bugtrack.project;

import io.github.nmaksimka.bugtrack.bug.BugRepository;
import io.github.nmaksimka.bugtrack.bug.BugStatus;
import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.common.ForbiddenException;
import io.github.nmaksimka.bugtrack.common.NotFoundException;
import io.github.nmaksimka.bugtrack.user.User;
import io.github.nmaksimka.bugtrack.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ProjectService {

    private static final Set<BugStatus> FINAL_STATUSES =
            EnumSet.of(BugStatus.CLOSED, BugStatus.REJECTED, BugStatus.DUPLICATE);

    private final ProjectRepository projects;
    private final MembershipRepository memberships;
    private final ProjectHistoryRepository history;
    private final UserRepository users;
    private final BugRepository bugs;

    public ProjectService(ProjectRepository projects, MembershipRepository memberships,
                          ProjectHistoryRepository history, UserRepository users, BugRepository bugs) {
        this.projects = projects;
        this.memberships = memberships;
        this.history = history;
        this.users = users;
        this.bugs = bugs;
    }

    @Transactional(readOnly = true)
    public List<ProjectSummary> listForUser(UUID userId) {
        return memberships.findActiveByUser(userId).stream()
                .map(m -> new ProjectSummary(
                        m.getProject().getId(),
                        m.getProject().getName(),
                        m.getProject().getKey(),
                        m.getProject().getDeadline(),
                        bugs.countOpen(m.getProject().getId(), FINAL_STATUSES),
                        m.getRoles()))
                .toList();
    }

    @Transactional
    public Project create(UUID creatorId, String name, String key, String description,
                          String repository, LocalDate deadline) {
        String normalizedKey = key.trim().toUpperCase();
        if (projects.existsByKey(normalizedKey)) {
            throw new DomainException("Проект с ключом " + normalizedKey + " уже есть");
        }
        User creator = users.findById(creatorId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Project project = new Project(name, normalizedKey, description, repository, deadline);
        Membership manager = project.addMember(creator, EnumSet.of(ProjectRole.MANAGER));
        projects.save(project);
        history.save(new ProjectHistoryEntry(project, manager, "Проект создан", null, project.getName(), null));
        return project;
    }

    @Transactional(readOnly = true)
    public Membership requireMembership(UUID userId, UUID projectId) {
        return memberships.findActive(userId, projectId)
                .orElseThrow(() -> new ForbiddenException("У вас нет доступа к этому проекту"));
    }

    @Transactional(readOnly = true)
    public ProjectDetails details(UUID userId, UUID projectId) {
        Membership me = requireMembership(userId, projectId);
        Project project = me.getProject();
        List<MemberView> members = project.getActiveMemberships().stream()
                .map(m -> new MemberView(m.getId(), m.getUser().getName(), m.getUser().getEmail(), m.getRoles()))
                .toList();
        return new ProjectDetails(project.getId(), project.getName(), project.getKey(), project.getDescription(),
                project.getRepository(), project.getDeadline(),
                bugs.countOpen(project.getId(), FINAL_STATUSES), me.getRoles(), members);
    }

    public record ProjectSummary(UUID id, String name, String key, LocalDate deadline,
                                 long openBugs, Set<ProjectRole> myRoles) {
    }

    public record MemberView(UUID membershipId, String name, String email, Set<ProjectRole> roles) {
    }

    public record ProjectDetails(UUID id, String name, String key, String description, String repository,
                                 LocalDate deadline, long openBugs, Set<ProjectRole> myRoles,
                                 List<MemberView> members) {
    }
}
