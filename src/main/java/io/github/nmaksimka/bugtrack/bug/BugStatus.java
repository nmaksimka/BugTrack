package io.github.nmaksimka.bugtrack.bug;

public enum BugStatus {
    NEW("Новый"),
    NEEDS_INFO("Нужно уточнение"),
    ASSIGNED("Назначен"),
    IN_PROGRESS("В работе"),
    IN_REVIEW("На проверке"),
    CLOSED("Закрыт"),
    REJECTED("Отклонён"),
    DUPLICATE("Дубликат");

    private final String title;

    BugStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFinal() {
        return this == CLOSED || this == REJECTED || this == DUPLICATE;
    }
}
