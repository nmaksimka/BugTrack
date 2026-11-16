package io.github.nmaksimka.bugtrack.bug;

public record BugDescription(
        String title,
        String description,
        String steps,
        String expected,
        String actual,
        String environment
) {
}
