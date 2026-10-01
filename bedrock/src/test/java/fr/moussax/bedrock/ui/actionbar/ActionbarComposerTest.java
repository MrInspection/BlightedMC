package fr.moussax.bedrock.ui.actionbar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionbarComposerTest {

    @Test
    @DisplayName("Expects the highest priority exclusive section to win over lower priority and normal sections")
    void testHighestPriorityExclusiveWins() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection lowPriorityExclusive = ActionbarSection.exclusive(
                "low_exclusive",
                10,
                _ -> "Low Priority Exclusive",
                _ -> true
        );

        ActionbarSection highPriorityExclusive = ActionbarSection.exclusive(
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

        ActionbarSection emptyHighExclusive = ActionbarSection.exclusive(
                "empty_high",
                200,
                _ -> "",
                _ -> true
        );

        ActionbarSection activeLowExclusive = ActionbarSection.exclusive(
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

    @Test
    @DisplayName("Expects a slot alert to override only its designated section while other sections remain visible")
    void testSlotAlertOverridesTargetSectionOnly() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection gems = ActionbarSection.of("gems", 0, _ -> "100 Gems");
        ActionbarSection mana = ActionbarSection.of("mana", 10, _ -> "50/100 Mana");

        composer.registerSection(gems);
        composer.registerSection(mana);

        composer.sendSlotAlert("mana", "§cNOT ENOUGH MANA", java.time.Duration.ofSeconds(5));

        String result = composer.compile(null);

        assertEquals("100 Gems     §cNOT ENOUGH MANA", result);
    }

    @Test
    @DisplayName("Expects a slot alert to fall back to a modal alert if the target section is not registered")
    void testSlotAlertFallsBackToModalWhenSectionMissing() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection gems = ActionbarSection.of("gems", 0, _ -> "100 Gems");
        composer.registerSection(gems);

        composer.sendSlotAlert("unregistered_slot", "Fallback Alert", java.time.Duration.ofSeconds(5));

        String result = composer.compile(null);

        assertEquals("Fallback Alert", result);
    }

    @Test
    @DisplayName("Expects a modal alert to override all standard and exclusive sections")
    void testModalAlertOverridesAll() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection exclusive = ActionbarSection.exclusive("exclusive", 100, _ -> "Exclusive HUD", _ -> true);
        composer.registerSection(exclusive);

        composer.sendModalAlert("Modal Broadcast", java.time.Duration.ofSeconds(5));

        String result = composer.compile(null);

        assertEquals("Modal Broadcast", result);
    }

    @Test
    @DisplayName("Expects clearAlerts to wipe active modal and slot alerts, restoring base sections")
    void testClearAlertsRestoresBaseSections() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection gems = ActionbarSection.of("gems", 0, _ -> "100 Gems");
        composer.registerSection(gems);

        composer.sendModalAlert("Modal Broadcast", java.time.Duration.ofSeconds(5));
        assertEquals("Modal Broadcast", composer.compile(null));

        composer.clearAlerts();

        assertEquals("100 Gems", composer.compile(null));
    }

    @Test
    @DisplayName("Expects ActionbarSection.builder to construct valid standard and exclusive sections")
    void testSectionBuilder() {
        ActionbarSection standard = ActionbarSection.builder("mana")
                .order(5)
                .text("50/100 Mana")
                .build();

        ActionbarSection exclusive = ActionbarSection.builder("mod")
                .exclusive(100)
                .render(_ -> "Mod Active")
                .build();

        assertEquals("mana", standard.id());
        assertEquals(5, standard.priority());
        assertFalse(standard.exclusive());
        assertEquals("50/100 Mana", standard.textSupplier().apply(null));

        assertEquals("mod", exclusive.id());
        assertEquals(100, exclusive.priority());
        assertTrue(exclusive.exclusive());
        assertEquals("Mod Active", exclusive.textSupplier().apply(null));
    }

    @Test
    @DisplayName("Expects local sections to merge with global sections and override globals with identical ids")
    void testGlobalAndLocalSectionsMergeAndOverride() {
        java.util.Map<String, ActionbarSection> globals = new java.util.HashMap<>();
        globals.put("gems", ActionbarSection.of("gems", 0, _ -> "Global Gems"));
        globals.put("zone", ActionbarSection.of("zone", 10, _ -> "Wilderness"));

        ActionbarComposer composer = new ActionbarComposer(() -> globals);

        // Local section overriding "gems"
        ActionbarSection localGems = ActionbarSection.of("gems", 0, _ -> "VIP Gems");
        // Local section adding "rank"
        ActionbarSection localRank = ActionbarSection.of("rank", 20, _ -> "[VIP]");

        composer.registerSection(localGems);
        composer.registerSection(localRank);

        String result = composer.compile(null);

        assertEquals("VIP Gems     Wilderness     [VIP]", result);
    }

    @Test
    @DisplayName("Expects custom separator to be used between standard sections")
    void testCustomSeparator() {
        ActionbarComposer composer = new ActionbarComposer();
        composer.setSeparator(" | ");

        composer.registerSection(ActionbarSection.of("section_one", 1, _ -> "Section 1"));
        composer.registerSection(ActionbarSection.of("section_two", 2, _ -> "Section 2"));

        assertEquals("Section 1 | Section 2", composer.compile(null));
    }

    @Test
    @DisplayName("Expects a newer alert with identical priority to supersede an older active alert")
    void testNewerAlertSupersedesOlderAlertWithSamePriority() throws InterruptedException {
        ActionbarComposer composer = new ActionbarComposer();

        composer.sendModalAlert("Rebooting in 5s", 0, java.time.Duration.ofSeconds(5));
        assertEquals("Rebooting in 5s", composer.compile(null));

        // Simulate 1 millisecond elapsed
        Thread.sleep(2);

        composer.sendModalAlert("Rebooting in 4s", 0, java.time.Duration.ofSeconds(4));
        assertEquals("Rebooting in 4s", composer.compile(null));
    }

    @Test
    @DisplayName("Expects dynamic modal alert to evaluate supplier on compile passes")
    void testDynamicSupplierModalAlert() {
        ActionbarComposer composer = new ActionbarComposer();

        java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(5);
        composer.sendModalAlert(_ -> "Rebooting in " + counter.get() + "s", 0, java.time.Duration.ofSeconds(5));

        assertEquals("Rebooting in 5s", composer.compile(null));

        counter.set(4);
        assertEquals("Rebooting in 4s", composer.compile(null));

        counter.set(3);
        assertEquals("Rebooting in 3s", composer.compile(null));
    }

    @Test
    @DisplayName("Expects automated countdown alert to format remaining seconds")
    void testCountdownAlert() {
        TimedAlert alert = TimedAlert.countdown("Rebooting in %ds...", 0, 5);
        assertEquals("Rebooting in 5s...", alert.message(null));
    }

    @Test
    @DisplayName("Expects per-section custom separator to override default separator between adjacent sections")
    void testPerSectionCustomSeparator() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection healthSection = ActionbarSection.builder("health")
                .order(1)
                .separator(" • ")
                .text("Health: 20")
                .build();

        ActionbarSection manaSection = ActionbarSection.builder("mana")
                .order(2)
                .separator(" | ")
                .text("Mana: 100")
                .build();

        ActionbarSection gemsSection = ActionbarSection.builder("gems")
                .order(3)
                .text("Gems: 50")
                .build();

        ActionbarSection zoneSection = ActionbarSection.builder("zone")
                .order(4)
                .text("Zone: Spawn")
                .build();

        composer.registerSection(healthSection);
        composer.registerSection(manaSection);
        composer.registerSection(gemsSection);
        composer.registerSection(zoneSection);

        // healthSection specifies " • ", manaSection specifies " | ", gemsSection uses default "     "
        assertEquals("Health: 20 • Mana: 100 | Gems: 50     Zone: Spawn", composer.compile(null));
    }

    @Test
    @DisplayName("Expects custom separator on last active section to not be rendered as a trailing separator")
    void testCustomSeparatorOnLastSectionDoesNotLeaveTrailingSeparator() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection healthSection = ActionbarSection.builder("health")
                .order(1)
                .text("Health: 20")
                .build();

        ActionbarSection manaSection = ActionbarSection.builder("mana")
                .order(2)
                .separator(" | ")
                .text("Mana: 100")
                .build();

        composer.registerSection(healthSection);
        composer.registerSection(manaSection);

        assertEquals("Health: 20     Mana: 100", composer.compile(null));
    }

    @Test
    @DisplayName("Expects inactive section with custom separator to not affect delimiters between active sections")
    void testInactiveSectionWithCustomSeparatorDoesNotAffectActiveSeparators() {
        ActionbarComposer composer = new ActionbarComposer();

        ActionbarSection healthSection = ActionbarSection.builder("health")
                .order(1)
                .text("Health: 20")
                .build();

        ActionbarSection inactiveSection = ActionbarSection.builder("inactive")
                .order(2)
                .separator(" | ")
                .render(_ -> null)
                .build();

        ActionbarSection zoneSection = ActionbarSection.builder("zone")
                .order(3)
                .text("Zone: Spawn")
                .build();

        composer.registerSection(healthSection);
        composer.registerSection(inactiveSection);
        composer.registerSection(zoneSection);

        // Since inactiveSection produces no text, healthSection directly precedes zoneSection and healthSection's default delimiter is used
        assertEquals("Health: 20     Zone: Spawn", composer.compile(null));
    }
}
