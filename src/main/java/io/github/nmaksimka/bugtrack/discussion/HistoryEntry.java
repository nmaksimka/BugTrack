package io.github.nmaksimka.bugtrack.discussion;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.bug.Bug;
import io.github.nmaksimka.bugtrack.project.Membership;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "history_entries")
public class HistoryEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bug_id")
    private Bug bug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private Membership author;

    @Column(nullable = false)
    private String action;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected HistoryEntry() {
    }

    public HistoryEntry(Bug bug, Membership author, String action, String oldValue, String newValue) {
        this.bug = bug;
        this.author = author;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createdAt = Instant.now();
    }

    public Bug getBug() {
        return bug;
    }

    public Membership getAuthor() {
        return author;
    }

    public String getAction() {
        return action;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
