package io.github.nmaksimka.bugtrack.bug;

import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.project.Membership;
import io.github.nmaksimka.bugtrack.project.Project;
import io.github.nmaksimka.bugtrack.project.ProjectRole;
import io.github.nmaksimka.bugtrack.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BugTest {

    private Project project;
    private Membership tester;
    private Membership developer;
    private Membership otherDeveloper;

    @BeforeEach
    void setUp() {
        project = new Project("Веб-портал", "bt", null, null, null);
        tester = project.addMember(new User("Анна", "anna@test.ru", "x"), EnumSet.of(ProjectRole.TESTER));
        developer = project.addMember(new User("Иван", "ivan@test.ru", "x"), EnumSet.of(ProjectRole.DEVELOPER));
        otherDeveloper = project.addMember(new User("Олег", "oleg@test.ru", "x"), EnumSet.of(ProjectRole.DEVELOPER));
    }

    @Test
    void newBugGetsNextNumberAndStatusNew() {
        Bug first = newBug();
        Bug second = newBug();

        assertThat(first.getStatus()).isEqualTo(BugStatus.NEW);
        assertThat(first.getCode()).isEqualTo("BT-1");
        assertThat(second.getCode()).isEqualTo("BT-2");
    }

    @Test
    void fullHappyPathEndsClosed() {
        Bug bug = newBug();

        bug.accept(Priority.HIGH, Severity.MAJOR, LocalDate.now().plusDays(3), null);
        bug.startWork(developer);
        bug.submitForReview(developer);
        bug.close();

        assertThat(bug.getStatus()).isEqualTo(BugStatus.CLOSED);
        assertThat(bug.getAssignee()).isEqualTo(developer);
    }

    @Test
    void returnedBugGoesBackToWorkWithFlag() {
        Bug bug = inReview();

        bug.returnToWork();

        assertThat(bug.getStatus()).isEqualTo(BugStatus.IN_PROGRESS);
        assertThat(bug.isReturned()).isTrue();
    }

    @Test
    void cannotTakeBugAssignedToSomeoneElse() {
        Bug bug = newBug();
        bug.accept(Priority.LOW, Severity.MINOR, null, developer);

        assertThatThrownBy(() -> bug.startWork(otherDeveloper)).isInstanceOf(DomainException.class);
    }

    @Test
    void onlyAssigneeSubmitsForReview() {
        Bug bug = newBug();
        bug.accept(Priority.LOW, Severity.MINOR, null, developer);
        bug.startWork(developer);

        assertThatThrownBy(() -> bug.submitForReview(otherDeveloper)).isInstanceOf(DomainException.class);
    }

    @Test
    void clarificationReturnsToNewAfterEdit() {
        Bug bug = newBug();
        bug.requestClarification();

        bug.edit(new BugDescription("Новое", null, "шаги", "ждём", "видим", "Chrome"));

        assertThat(bug.getStatus()).isEqualTo(BugStatus.NEW);
        assertThat(bug.getTitle()).isEqualTo("Новое");
    }

    @Test
    void closedBugCannotBeEdited() {
        Bug bug = inReview();
        bug.close();

        assertThatThrownBy(() -> bug.edit(description())).isInstanceOf(DomainException.class);
    }

    @Test
    void triageOnlyFromNew() {
        Bug bug = inReview();

        assertThatThrownBy(bug::reject).isInstanceOf(DomainException.class);
    }

    @Test
    void overdueOnlyWhenOpenAndPastDeadline() {
        Bug bug = newBug();
        bug.accept(Priority.LOW, Severity.MINOR, LocalDate.of(2026, 1, 1), null);

        assertThat(bug.isOverdue(LocalDate.of(2026, 1, 2))).isTrue();
        assertThat(bug.isOverdue(LocalDate.of(2026, 1, 1))).isFalse();
    }

    private Bug inReview() {
        Bug bug = newBug();
        bug.accept(Priority.HIGH, Severity.MAJOR, null, developer);
        bug.startWork(developer);
        bug.submitForReview(developer);
        return bug;
    }

    private Bug newBug() {
        return new Bug(project, tester, description(), Priority.MEDIUM, Severity.MAJOR);
    }

    private static BugDescription description() {
        return new BugDescription("Не сохраняется профиль", null, "Открыть профиль → сохранить",
                "Сохранено", "Не сохранено", "Windows 11 · Chrome");
    }
}
