package io.github.nmaksimka.bugtrack.bug;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.common.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bug_links")
public class BugLink extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id")
    private Bug source;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id")
    private Bug target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkType type;

    protected BugLink() {
    }

    public BugLink(Bug source, Bug target, LinkType type) {
        if (source.getId().equals(target.getId())) {
            throw new DomainException("Нельзя связать баг сам с собой");
        }
        if (!source.getProject().getId().equals(target.getProject().getId())) {
            throw new DomainException("Связывать можно только баги одного проекта");
        }
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public Bug getSource() {
        return source;
    }

    public Bug getTarget() {
        return target;
    }

    public LinkType getType() {
        return type;
    }
}
