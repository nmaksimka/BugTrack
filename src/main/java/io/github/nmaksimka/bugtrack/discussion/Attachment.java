package io.github.nmaksimka.bugtrack.discussion;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.bug.Bug;
import io.github.nmaksimka.bugtrack.fix.VerificationResult;
import io.github.nmaksimka.bugtrack.project.Membership;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "attachments")
public class Attachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bug_id")
    private Bug bug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_result_id")
    private VerificationResult verificationResult;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by_id")
    private Membership uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    protected Attachment() {
    }

    public Attachment(Bug bug, String fileName, String contentType, long size,
                      String storageKey, Membership uploadedBy) {
        this.bug = bug;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.storageKey = storageKey;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = Instant.now();
    }

    public void attachTo(Comment comment) {
        this.comment = comment;
    }

    public void attachTo(VerificationResult verificationResult) {
        this.verificationResult = verificationResult;
    }

    public Bug getBug() {
        return bug;
    }

    public Comment getComment() {
        return comment;
    }

    public VerificationResult getVerificationResult() {
        return verificationResult;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public Membership getUploadedBy() {
        return uploadedBy;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }
}
