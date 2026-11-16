package io.github.nmaksimka.bugtrack.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectHistoryRepository extends JpaRepository<ProjectHistoryEntry, UUID> {

    List<ProjectHistoryEntry> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
