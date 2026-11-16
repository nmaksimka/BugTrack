package io.github.nmaksimka.bugtrack.fix;

import io.github.nmaksimka.bugtrack.common.BaseEntity;
import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.project.Membership;
import io.github.nmaksimka.bugtrack.release.Release;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "verification_results")
public class VerificationResult extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fix_attempt_id")
    private FixAttempt fixAttempt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Verdict verdict;

    @Column(name = "actual_result", nullable = false)
    private String actualResult;

    @Column(name = "return_reason")
    private String returnReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fix_release_id")
    private Release fixRelease;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tester_id")
    private Membership tester;

    @Column(name = "verified_at", nullable = false)
    private Instant verifiedAt;

    protected VerificationResult() {
    }

    private VerificationResult(FixAttempt fixAttempt, Verdict verdict, String actualResult,
                               String returnReason, Release fixRelease, Membership tester) {
        if (!fixAttempt.getReviewer().getId().equals(tester.getId())) {
            throw new DomainException("Проверку выполняет назначенный проверяющий");
        }
        this.fixAttempt = fixAttempt;
        this.verdict = verdict;
        this.actualResult = actualResult;
        this.returnReason = returnReason;
        this.fixRelease = fixRelease;
        this.tester = tester;
        this.verifiedAt = Instant.now();
    }

    public static VerificationResult confirm(FixAttempt attempt, String actualResult,
                                             Release fixRelease, Membership tester) {
        if (fixRelease == null) {
            throw new DomainException("Укажите релиз исправления");
        }
        VerificationResult result = new VerificationResult(attempt, Verdict.CONFIRMED, actualResult, null, fixRelease, tester);
        attempt.getBug().close();
        return result;
    }

    public static VerificationResult returnToDeveloper(FixAttempt attempt, String actualResult,
                                                       String reason, Membership tester) {
        if (reason == null || reason.isBlank()) {
            throw new DomainException("Укажите причину возврата");
        }
        VerificationResult result = new VerificationResult(attempt, Verdict.RETURNED, actualResult, reason, null, tester);
        attempt.getBug().returnToWork();
        return result;
    }

    public FixAttempt getFixAttempt() {
        return fixAttempt;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public String getActualResult() {
        return actualResult;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public Release getFixRelease() {
        return fixRelease;
    }

    public Membership getTester() {
        return tester;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }
}
