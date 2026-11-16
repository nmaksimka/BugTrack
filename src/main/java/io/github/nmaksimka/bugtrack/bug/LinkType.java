package io.github.nmaksimka.bugtrack.bug;

public enum LinkType {
    DUPLICATE("Дубликат"),
    RELATED("Связан"),
    REGRESSION("Регрессия"),
    BLOCKS("Блокирует");

    private final String title;

    LinkType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
