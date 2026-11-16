package io.github.nmaksimka.bugtrack.fix;

import java.util.List;

public record FixDetails(
        String description,
        List<String> changeLinks,
        String noLinkReason,
        String build,
        String environment,
        String instruction
) {
}
