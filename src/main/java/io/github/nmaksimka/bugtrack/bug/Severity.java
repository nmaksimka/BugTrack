package io.github.nmaksimka.bugtrack.bug;

public enum Severity {
    CRITICAL("Критическая"),
    MAJOR("Значительная"),
    MINOR("Незначительная");

    private final String title;

    Severity(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
