package fr.moussax.bedrock.ui.actionbar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionbarComposerTest {

    @Test
    @DisplayName("Expects the highest priority exclusive section to win over lower priority and normal sections")
    void testHighestPriorityExclusiveWins() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection lowPriorityExclusive = ActionbarSection.exclusiveOf(
                "low_exclusive",
                10,
                _ -> "Low Priority Exclusive",
                _ -> true
        );

        ActionbarSection highPriorityExclusive = ActionbarSection.exclusiveOf(
                "high_exclusive",
                100,
                _ -> "High Priority Exclusive",
                _ -> true
        );

        ActionbarSection normalSection = ActionbarSection.of(
                "normal",
                0,
                _ -> "Normal Text",
                _ -> true
        );

        composer.registerSection(lowPriorityExclusive);
        composer.registerSection(highPriorityExclusive);
        composer.registerSection(normalSection);

        String result = composer.compile(null);

        assertEquals("High Priority Exclusive", result);
    }

    @Test
    @DisplayName("Expects an empty exclusive section to fall back to the next available exclusive section")
    void testEmptyExclusiveFallsBack() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection emptyHighExclusive = ActionbarSection.exclusiveOf(
                "empty_high",
                200,
                _ -> "",
                _ -> true
        );

        ActionbarSection activeLowExclusive = ActionbarSection.exclusiveOf(
                "active_low",
                50,
                _ -> "Active Low Exclusive",
                _ -> true
        );

        composer.registerSection(emptyHighExclusive);
        composer.registerSection(activeLowExclusive);

        String result = composer.compile(null);

        assertEquals("Active Low Exclusive", result);
    }

    @Test
    @DisplayName("Expects normal sections to render in ascending priority order when no exclusive section is active")
    void testNormalSectionsFollowPriority() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection firstNormal = ActionbarSection.of(
                "first",
                10,
                _ -> "First Normal",
                _ -> true
        );

        ActionbarSection secondNormal = ActionbarSection.of(
                "second",
                20,
                _ -> "Second Normal",
                _ -> true
        );

        composer.registerSection(secondNormal);
        composer.registerSection(firstNormal);

        String result = composer.compile(null);

        assertEquals("First Normal     Second Normal", result);
    }
}
