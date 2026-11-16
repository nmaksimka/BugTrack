package io.github.nmaksimka.bugtrack.bug;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.project.Membership;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "triage_decisions")
public class Triage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bug_id")
    private Bug bug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TriageDecision decision;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private Membership author;

    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;

    protected Triage() {
    }

    public Triage(Bug bug, TriageDecision decision, String reason, Membership author) {
        this.bug = bug;
        this.decision = decision;
        this.reason = reason;
        this.author = author;
        this.decidedAt = Instant.now();
    }

    public Bug getBug() {
        return bug;
    }

    public TriageDecision getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

    public Membership getAuthor() {
        return author;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }
}
