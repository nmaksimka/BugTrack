package io.github.nmaksimka.bugtrack.release;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.bug.Bug;
import io.github.nmaksimka.bugtrack.bug.BugStatus;
import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.project.Project;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "releases")
public class Release extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private String version;

    private String description;

    @Column(name = "planned_date")
    private LocalDate plannedDate;

    @Column(name = "released_at")
    private Instant releasedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReleaseStatus status;

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL)
    private List<ReleaseItem> items = new ArrayList<>();

    protected Release() {
    }

    public Release(Project project, String version, String description, LocalDate plannedDate) {
        this.project = project;
        this.version = version.trim();
        this.description = description;
        this.plannedDate = plannedDate;
        this.status = ReleaseStatus.PLANNED;
    }

    public ReleaseItem addBug(Bug bug, boolean blocksRelease) {
        requirePlanned();
        if (!bug.getProject().getId().equals(project.getId())) {
            throw new DomainException("Баг из другого проекта");
        }
        boolean alreadyIncluded = items.stream()
                .anyMatch(item -> item.isIncluded() && item.getBug().getId().equals(bug.getId()));
        if (alreadyIncluded) {
            throw new DomainException("Баг уже в составе релиза");
        }
        ReleaseItem item = new ReleaseItem(this, bug, blocksRelease);
        items.add(item);
        return item;
    }

    public List<ReleaseItem> findObstacles() {
        return items.stream()
                .filter(ReleaseItem::isIncluded)
                .filter(item -> item.getBug().getStatus() != BugStatus.CLOSED)
                .toList();
    }

    public boolean isReady() {
        return status == ReleaseStatus.PLANNED && findObstacles().isEmpty();
    }

    public void release() {
        requirePlanned();
        if (!findObstacles().isEmpty()) {
            throw new DomainException("В составе есть незавершённые баги");
        }
        status = ReleaseStatus.RELEASED;
        releasedAt = Instant.now();
    }

    public void cancel() {
        requirePlanned();
        status = ReleaseStatus.CANCELLED;
    }

    void requirePlanned() {
        if (status != ReleaseStatus.PLANNED) {
            throw new DomainException("Релиз уже «" + status.getTitle() + "», состав менять нельзя");
        }
    }

    public Project getProject() {
        return project;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public Instant getReleasedAt() {
        return releasedAt;
    }

    public ReleaseStatus getStatus() {
        return status;
    }

    public List<ReleaseItem> getItems() {
        return List.copyOf(items);
    }
}
