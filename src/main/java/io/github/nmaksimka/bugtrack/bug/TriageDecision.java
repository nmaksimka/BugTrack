package io.github.nmaksimka.bugtrack.bug;

public enum TriageDecision {
    ACCEPTED("Принят"),
    NEEDS_INFO("Запрошено уточнение"),
    DUPLICATE("Дубликат"),
    REJECTED("Отклонён");

    private final String title;

    TriageDecision(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
