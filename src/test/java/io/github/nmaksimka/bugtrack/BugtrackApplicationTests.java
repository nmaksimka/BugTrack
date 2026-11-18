package io.github.nmaksimka.bugtrack;

import io.github.nmaksimka.bugtrack.project.ProjectService;
import io.github.nmaksimka.bugtrack.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class BugtrackApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.6");

    @Autowired
    UserRepository users;

    @Autowired
    ProjectService projectService;

    @Test
    void demoDataMatchesSchemaAndIsVisibleToMembers() {
        var anna = users.findByEmail("anna@bugtrack.local").orElseThrow();

        var projects = projectService.listForUser(anna.getId());

        assertThat(projects).extracting(ProjectService.ProjectSummary::key).containsExactly("BT", "MA");
        assertThat(projects).filteredOn(p -> p.key().equals("BT"))
                .singleElement()
                .satisfies(p -> assertThat(p.openBugs()).isEqualTo(3));
    }
}
