package io.github.nmaksimka.bugtrack.fix;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FixAttemptRepository extends JpaRepository<FixAttempt, UUID> {

    List<FixAttempt> findByBugIdOrderByNumberDesc(UUID bugId);

    int countByBugId(UUID bugId);
}
