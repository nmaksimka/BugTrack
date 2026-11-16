package io.github.nmaksimka.bugtrack.bug;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.project.Membership;
import io.github.nmaksimka.bugtrack.project.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "bugs")
public class Bug extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String steps;

    @Column(nullable = false)
    private String expected;

    @Column(nullable = false)
    private String actual;

    @Column(nullable = false)
    private String environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    private LocalDate deadline;

    @Column(nullable = false)
    private boolean returned;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private Membership author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Membership assignee;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    protected Bug() {
    }

    public Bug(Project project, Membership author, BugDescription description,
               Priority priority, Severity severity) {
        this.project = project;
        this.number = project.nextBugNumber();
        this.author = author;
        this.status = BugStatus.NEW;
        this.priority = priority;
        this.severity = severity;
        this.returned = false;
        this.createdAt = Instant.now();
        applyDescription(description);
    }

    public void edit(BugDescription description) {
        requireNotFinal();
        applyDescription(description);
        if (status == BugStatus.NEEDS_INFO) {
            status = BugStatus.NEW;
        }
    }

    public void accept(Priority priority, Severity severity, LocalDate deadline, Membership assignee) {
        requireStatus(BugStatus.NEW);
        this.priority = priority;
        this.severity = severity;
        this.deadline = deadline;
        this.status = BugStatus.ASSIGNED;
        this.assignee = assignee;
        touch();
    }

    public void requestClarification() {
        requireStatus(BugStatus.NEW);
        status = BugStatus.NEEDS_INFO;
        touch();
    }

    public void markDuplicate() {
        requireStatus(BugStatus.NEW);
        status = BugStatus.DUPLICATE;
        touch();
    }

    public void reject() {
        requireStatus(BugStatus.NEW);
        status = BugStatus.REJECTED;
        touch();
    }

    public void assign(Membership developer) {
        if (status != BugStatus.ASSIGNED && status != BugStatus.IN_PROGRESS) {
            throw new DomainException("Назначить исполнителя можно только принятому багу");
        }
        assignee = developer;
        touch();
    }

    public void startWork(Membership developer) {
        if (status != BugStatus.ASSIGNED && !(status == BugStatus.IN_PROGRESS && returned)) {
            throw new DomainException("Начать работу можно только над назначенным багом");
        }
        if (assignee != null && !assignee.getId().equals(developer.getId())) {
            throw new DomainException("Баг уже закреплён за другим разработчиком");
        }
        assignee = developer;
        status = BugStatus.IN_PROGRESS;
        touch();
    }

    public void submitForReview(Membership developer) {
        requireStatus(BugStatus.IN_PROGRESS);
        if (assignee == null || !assignee.getId().equals(developer.getId())) {
            throw new DomainException("Передать на проверку может только исполнитель");
        }
        status = BugStatus.IN_REVIEW;
        touch();
    }

    public void close() {
        requireStatus(BugStatus.IN_REVIEW);
        status = BugStatus.CLOSED;
        touch();
    }

    public void returnToWork() {
        requireStatus(BugStatus.IN_REVIEW);
        status = BugStatus.IN_PROGRESS;
        returned = true;
        touch();
    }

    public void changePlan(Priority priority, LocalDate deadline) {
        requireNotFinal();
        this.priority = priority;
        this.deadline = deadline;
        touch();
    }

    public boolean isOverdue(LocalDate today) {
        return deadline != null && !status.isFinal() && deadline.isBefore(today);
    }

    public String getCode() {
        return project.getKey() + "-" + number;
    }

    private void applyDescription(BugDescription description) {
        this.title = description.title();
        this.description = description.description();
        this.steps = description.steps();
        this.expected = description.expected();
        this.actual = description.actual();
        this.environment = description.environment();
        touch();
    }

    private void requireStatus(BugStatus required) {
        if (status != required) {
            throw new DomainException("Действие недоступно в статусе «" + status.getTitle() + "»");
        }
    }

    private void requireNotFinal() {
        if (status.isFinal()) {
            throw new DomainException("Баг уже закрыт");
        }
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    public Project getProject() {
        return project;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSteps() {
        return steps;
    }

    public String getExpected() {
        return expected;
    }

    public String getActual() {
        return actual;
    }

    public String getEnvironment() {
        return environment;
    }

    public BugStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public Severity getSeverity() {
        return severity;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public boolean isReturned() {
        return returned;
    }

    public Membership getAuthor() {
        return author;
    }

    public Membership getAssignee() {
        return assignee;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }
}
