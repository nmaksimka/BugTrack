package io.github.nmaksimka.bugtrack.discussion;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.bug.Bug;
import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.project.Membership;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bug_id")
    private Bug bug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private Membership author;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private boolean obstacle;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Comment() {
    }

    private Comment(Bug bug, Membership author, String text, boolean obstacle) {
        if (text == null || text.isBlank()) {
            throw new DomainException("Комментарий не может быть пустым");
        }
        this.bug = bug;
        this.author = author;
        this.text = text.trim();
        this.obstacle = obstacle;
        this.createdAt = Instant.now();
    }

    public static Comment of(Bug bug, Membership author, String text) {
        return new Comment(bug, author, text, false);
    }

    public static Comment obstacle(Bug bug, Membership author, String text) {
        return new Comment(bug, author, text, true);
    }

    public Bug getBug() {
        return bug;
    }

    public Membership getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
