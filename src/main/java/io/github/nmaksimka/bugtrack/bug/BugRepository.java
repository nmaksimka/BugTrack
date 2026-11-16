package io.github.nmaksimka.bugtrack.bug;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.UUID;

public interface BugRepository extends JpaRepository<Bug, UUID> {

    @Query("select count(b) from Bug b where b.project.id = :projectId and b.status not in :finalStatuses")
    long countOpen(UUID projectId, Collection<BugStatus> finalStatuses);
}
