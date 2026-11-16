package io.github.nmaksimka.bugtrack.project;

public enum ProjectRole {
    MANAGER("Руководитель проекта"),
    TEAM_LEAD("Тимлид"),
    DEVELOPER("Разработчик"),
    TESTER("Тестировщик");

    private final String title;

    ProjectRole(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
