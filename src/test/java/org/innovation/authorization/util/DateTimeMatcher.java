package org.innovation.authorization.util;

import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import java.time.LocalDateTime;

import org.mockito.ArgumentMatcher;

public class DateTimeMatcher implements ArgumentMatcher<LocalDateTime> {

    private final LocalDateTime from;

    private final LocalDateTime to;

    public static LocalDateTime dateTime(LocalDateTime from, LocalDateTime to) {
        mockingProgress().getArgumentMatcherStorage().reportMatcher(new DateTimeMatcher(from, to));
        return LocalDateTime.now();
    }

    private DateTimeMatcher(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean matches(LocalDateTime argument) {
        if (from.isAfter(argument) || to.isBefore(argument)) {
            return false;
        }
        return true;
    }

}
