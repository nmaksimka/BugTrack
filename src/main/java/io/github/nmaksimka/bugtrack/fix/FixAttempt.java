package io.github.nmaksimka.bugtrack.fix;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.bug.Bug;
import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.project.Membership;
import io.github.nmaksimka.bugtrack.project.ProjectRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fix_attempts")
public class FixAttempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bug_id")
    private Bug bug;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "fix_attempt_links", joinColumns = @JoinColumn(name = "fix_attempt_id"))
    @OrderColumn(name = "position")
    @Column(name = "url")
    private List<String> changeLinks = new ArrayList<>();

    @Column(name = "no_link_reason")
    private String noLinkReason;

    @Column(nullable = false)
    private String build;

    @Column(nullable = false)
    private String environment;

    @Column(nullable = false)
    private String instruction;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "developer_id")
    private Membership developer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id")
    private Membership reviewer;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected FixAttempt() {
    }

    public FixAttempt(Bug bug, int number, FixDetails details, Membership developer, Membership reviewer) {
        if (details.changeLinks().isEmpty() && isBlank(details.noLinkReason())) {
            throw new DomainException("Добавьте ссылку на изменение или укажите, почему её нет");
        }
        if (reviewer.getId().equals(developer.getId())) {
            throw new DomainException("Проверяющий должен быть другим участником");
        }
        if (!reviewer.has(ProjectRole.TESTER)) {
            throw new DomainException("Проверяющий должен быть тестировщиком проекта");
        }
        this.bug = bug;
        this.number = number;
        this.description = details.description();
        this.changeLinks.addAll(details.changeLinks());
        this.noLinkReason = details.noLinkReason();
        this.build = details.build();
        this.environment = details.environment();
        this.instruction = details.instruction();
        this.developer = developer;
        this.reviewer = reviewer;
        this.submittedAt = Instant.now();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public Bug getBug() {
        return bug;
    }

    public int getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getChangeLinks() {
        return List.copyOf(changeLinks);
    }

    public String getNoLinkReason() {
        return noLinkReason;
    }

    public String getBuild() {
        return build;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getInstruction() {
        return instruction;
    }

    public Membership getDeveloper() {
        return developer;
    }

    public Membership getReviewer() {
        return reviewer;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
