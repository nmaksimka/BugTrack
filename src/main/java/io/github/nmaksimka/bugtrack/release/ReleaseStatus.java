package io.github.nmaksimka.bugtrack.release;

public enum ReleaseStatus {
    PLANNED("Планируется"),
    RELEASED("Выпущен"),
    CANCELLED("Отменён");

    private final String title;

    ReleaseStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
