package io.github.nmaksimka.bugtrack.fix;

public enum Verdict {
    CONFIRMED("Исправление подтверждено"),
    RETURNED("Возвращено на доработку");

    private final String title;

    Verdict(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
