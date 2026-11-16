package io.github.nmaksimka.bugtrack.release;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.bug.Bug;
import io.github.nmaksimka.bugtrack.common.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "release_items")
public class ReleaseItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "release_id")
    private Release release;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bug_id")
    private Bug bug;

    @Column(name = "blocks_release", nullable = false)
    private boolean blocksRelease;

    @Column(nullable = false)
    private boolean excluded;

    @Column(name = "exclusion_reason")
    private String exclusionReason;

    protected ReleaseItem() {
    }

    ReleaseItem(Release release, Bug bug, boolean blocksRelease) {
        this.release = release;
        this.bug = bug;
        this.blocksRelease = blocksRelease;
        this.excluded = false;
    }

    public ReleaseItem moveTo(Release target, String reason) {
        exclude(reason);
        return target.addBug(bug, blocksRelease);
    }

    public void exclude(String reason) {
        release.requirePlanned();
        if (reason == null || reason.isBlank()) {
            throw new DomainException("Укажите причину изменения состава");
        }
        excluded = true;
        exclusionReason = reason.trim();
    }

    public boolean isIncluded() {
        return !excluded;
    }

    public Release getRelease() {
        return release;
    }

    public Bug getBug() {
        return bug;
    }

    public boolean isBlocksRelease() {
        return blocksRelease;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public String getExclusionReason() {
        return exclusionReason;
    }
}
