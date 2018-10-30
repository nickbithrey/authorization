package org.innovation.authorization.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.LocalizedMatcher;
import org.mockito.internal.progress.ThreadSafeMockingProgress;

public class DateTimeMatcherTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private final LocalDateTime arg1 = LocalDateTime.of(2018, 10, 29, 12, 0, 0);

    private final LocalDateTime arg2 = LocalDateTime.of(2018, 10, 29, 12, 1, 1);

    @Test
    public void testStaticMethod() {
        // assert that matcher currently is not registered
        assertThat(ThreadSafeMockingProgress.mockingProgress().getArgumentMatcherStorage().pullLocalizedMatchers())
                .as("registered matchers before registering %s", DateTimeMatcher.class.getSimpleName()).hasSize(0);

        // regiseter matcher
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime dateTime = DateTimeMatcher.dateTime(arg1, arg2);
        LocalDateTime to = LocalDateTime.now();

        softly.assertThat(dateTime).as("returned date time from matcher").isNotNull().isBetween(from, to);
        softly.assertThat(
                ThreadSafeMockingProgress.mockingProgress().getArgumentMatcherStorage().pullLocalizedMatchers())
                .as("registered matchers after registering %s", DateTimeMatcher.class.getSimpleName())
                .anyMatch(matcher -> matcher.getMatcher() instanceof DateTimeMatcher);

    }

    @Test
    public void testMatchesInside() {
        DateTimeMatcher.dateTime(arg1, arg2);

        ArgumentMatcher<LocalDateTime> matcher = getMatcher();
        boolean matches = matcher.matches(LocalDateTime.of(2018, 10, 29, 12, 0, 30));
        assertThat(matches).as("matches when within boundaries").isTrue();
    }

    @Test
    public void testMatchesBefore() {
        DateTimeMatcher.dateTime(arg1, arg2);

        ArgumentMatcher<LocalDateTime> matcher = getMatcher();
        boolean matches = matcher.matches(LocalDateTime.of(2018, 10, 29, 11, 59, 30));
        assertThat(matches).as("matches when before lower boundary").isFalse();
    }

    @Test
    public void testMatchesAfter() {
        DateTimeMatcher.dateTime(arg1, arg2);

        ArgumentMatcher<LocalDateTime> matcher = getMatcher();
        boolean matches = matcher.matches(LocalDateTime.of(2018, 10, 29, 12, 1, 30));
        assertThat(matches).as("matches when after upper boundary").isFalse();
    }

    @Test
    public void testMatchesLowerBoundary() {
        DateTimeMatcher.dateTime(arg1, arg2);

        ArgumentMatcher<LocalDateTime> matcher = getMatcher();
        boolean matches = matcher.matches(LocalDateTime.of(2018, 10, 29, 12, 0, 0));
        assertThat(matches).as("matches when equal to lower boundary").isTrue();
    }

    @Test
    public void testMatchesUpperBoundary() {
        DateTimeMatcher.dateTime(arg1, arg2);

        ArgumentMatcher<LocalDateTime> matcher = getMatcher();
        boolean matches = matcher.matches(LocalDateTime.of(2018, 10, 29, 12, 1, 0));
        assertThat(matches).as("matches when equal to upper boundary").isTrue();
    }

    private ArgumentMatcher<LocalDateTime> getMatcher() {
        List<LocalizedMatcher> localizedMatchers = ThreadSafeMockingProgress.mockingProgress()
                .getArgumentMatcherStorage().pullLocalizedMatchers().stream()
                .filter(matcher -> matcher.getMatcher() instanceof DateTimeMatcher).collect(Collectors.toList());
        assertThat(localizedMatchers).as("registered %s matchers", DateTimeMatcher.class.getSimpleName()).hasSize(1);
        return (ArgumentMatcher<LocalDateTime>) localizedMatchers.get(0).getMatcher();
    }

}
