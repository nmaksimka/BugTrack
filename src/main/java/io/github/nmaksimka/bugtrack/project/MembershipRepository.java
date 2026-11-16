package io.github.nmaksimka.bugtrack.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    @Query("""
            select m from Membership m join fetch m.project
            where m.user.id = :userId and m.active = true
            order by m.project.name
            """)
    List<Membership> findActiveByUser(UUID userId);

    @Query("""
            select m from Membership m
            where m.user.id = :userId and m.project.id = :projectId and m.active = true
            """)
    Optional<Membership> findActive(UUID userId, UUID projectId);
}
