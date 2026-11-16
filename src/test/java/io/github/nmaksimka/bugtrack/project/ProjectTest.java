package io.github.nmaksimka.bugtrack.project;

import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.user.User;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectTest {

    private final Project project = new Project(" Веб-портал ", " bt ", null, null, null);
    private final User anna = new User("Анна", "Anna@Test.ru ", "x");

    @Test
    void keyAndNameAreNormalized() {
        assertThat(project.getKey()).isEqualTo("BT");
        assertThat(project.getName()).isEqualTo("Веб-портал");
        assertThat(anna.getEmail()).isEqualTo("anna@test.ru");
    }

    @Test
    void memberCannotBeAddedTwice() {
        project.addMember(anna, EnumSet.of(ProjectRole.TESTER));

        assertThatThrownBy(() -> project.addMember(anna, EnumSet.of(ProjectRole.DEVELOPER)))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void disabledMemberCanBeReturnedWithNewRoles() {
        Membership membership = project.addMember(anna, EnumSet.of(ProjectRole.TESTER));
        membership.disable();

        Membership again = project.addMember(anna, EnumSet.of(ProjectRole.DEVELOPER));

        assertThat(again).isSameAs(membership);
        assertThat(again.isActive()).isTrue();
        assertThat(again.getRoles()).containsExactly(ProjectRole.DEVELOPER);
        assertThat(project.getActiveMemberships()).hasSize(1);
    }

    @Test
    void disabledMemberLosesRoles() {
        Membership membership = project.addMember(anna, EnumSet.of(ProjectRole.TESTER));

        membership.disable();

        assertThat(membership.has(ProjectRole.TESTER)).isFalse();
    }

    @Test
    void memberNeedsAtLeastOneRole() {
        Membership membership = project.addMember(anna, EnumSet.of(ProjectRole.TESTER));

        assertThatThrownBy(() -> membership.changeRoles(Set.of())).isInstanceOf(DomainException.class);
    }
}
