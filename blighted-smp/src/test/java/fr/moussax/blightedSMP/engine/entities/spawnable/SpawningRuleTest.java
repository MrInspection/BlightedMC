package fr.moussax.blightedSMP.engine.entities.spawnable;

import fr.moussax.blightedSMP.engine.entities.spawnable.condition.SpawnCondition;
import fr.moussax.blightedSMP.engine.entities.spawnable.engine.SpawnMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class SpawningRuleTest {

    private final SpawnCondition alwaysTrue = (_, _) -> true;
    private final SpawnCondition alwaysFalse = (_, _) -> false;

    @Test
    @DisplayName("anyOf should pass if at least one condition passes")
    void testAnyOf() {
        assertFalse(SpawnCondition.anyOf().testCanSpawnAt(null, null), "Empty anyOf should evaluate to false");

        assertTrue(SpawnCondition.anyOf(alwaysTrue).testCanSpawnAt(null, null));
        assertFalse(SpawnCondition.anyOf(alwaysFalse).testCanSpawnAt(null, null));

        assertTrue(SpawnCondition.anyOf(alwaysFalse, alwaysTrue, alwaysFalse).testCanSpawnAt(null, null));
        assertFalse(SpawnCondition.anyOf(alwaysFalse, alwaysFalse).testCanSpawnAt(null, null));

        // Short-circuiting verification
        AtomicBoolean evaluated = new AtomicBoolean(false);
        SpawnCondition probe = (_, _) -> {
            evaluated.set(true);
            return true;
        };

        boolean result = SpawnCondition.anyOf(alwaysTrue, probe).testCanSpawnAt(null, null);
        assertTrue(result);
        assertFalse(evaluated.get(), "anyOf should short-circuit upon first matching condition");
    }

    @Test
    @DisplayName("allOf should pass only if all conditions pass")
    void testAllOf() {
        assertTrue(SpawnCondition.allOf().testCanSpawnAt(null, null), "Empty allOf should evaluate to true");

        assertTrue(SpawnCondition.allOf(alwaysTrue, alwaysTrue).testCanSpawnAt(null, null));
        assertFalse(SpawnCondition.allOf(alwaysTrue, alwaysFalse).testCanSpawnAt(null, null));

        // Short-circuiting verification
        AtomicBoolean evaluated = new AtomicBoolean(false);
        SpawnCondition probe = (_, _) -> {
            evaluated.set(true);
            return true;
        };

        boolean result = SpawnCondition.allOf(alwaysFalse, probe).testCanSpawnAt(null, null);
        assertFalse(result);
        assertFalse(evaluated.get(), "allOf should short-circuit upon first failing condition");
    }

    @Test
    @DisplayName("noneOf should pass only if no conditions pass")
    void testNoneOf() {
        assertTrue(SpawnCondition.noneOf(alwaysFalse, alwaysFalse).testCanSpawnAt(null, null));
        assertFalse(SpawnCondition.noneOf(alwaysFalse, alwaysTrue).testCanSpawnAt(null, null));
    }

    @Test
    @DisplayName("Default combinators and, or, negate should evaluate correctly")
    void testDefaultCombinators() {
        assertTrue(alwaysTrue.and(alwaysTrue).testCanSpawnAt(null, null));
        assertFalse(alwaysTrue.and(alwaysFalse).testCanSpawnAt(null, null));

        assertTrue(alwaysFalse.or(alwaysTrue).testCanSpawnAt(null, null));
        assertFalse(alwaysFalse.or(alwaysFalse).testCanSpawnAt(null, null));

        assertFalse(alwaysTrue.negate().testCanSpawnAt(null, null));
        assertTrue(alwaysFalse.negate().testCanSpawnAt(null, null));
    }

    @Test
    @DisplayName("SpawningBuilder correctly configures probability, mode, and conditions")
    void testSpawningBuilder() {
        SpawningBuilder builder = new SpawningBuilder();

        assertEquals(SpawningBuilder.DEFAULT_PROBABILITY, builder.getProbability());
        assertEquals(SpawningBuilder.DEFAULT_MODE, builder.getMode());

        builder.probability(0.05)
                .mode(SpawnMode.INDEPENDENT)
                .anyOf(alwaysTrue, alwaysFalse)
                .condition(alwaysTrue);

        assertEquals(0.05, builder.getProbability(), 1e-6);
        assertEquals(SpawnMode.INDEPENDENT, builder.getMode());
        assertEquals(2, builder.getConditions().size());

        SpawnProfile profile = builder.buildProfile();
        assertNotNull(profile);
        assertEquals(2, profile.conditions().size());
    }

    @Test
    @DisplayName("SpawningBuilder should reject out-of-range probabilities")
    void testProbabilityValidation() {
        SpawningBuilder builder = new SpawningBuilder();
        assertThrows(IllegalArgumentException.class, () -> builder.probability(-0.01));
        assertThrows(IllegalArgumentException.class, () -> builder.probability(1.01));
    }

    @Test
    @DisplayName("SpawningBuilder should properly configure and validate elite affixes")
    void testAffixesConfiguration() {
        SpawningBuilder builder = new SpawningBuilder();
        assertEquals(0.0, builder.getAffixChance());
        assertEquals(1, builder.getMaxAffixes());

        builder.affixes(0.25, 3);
        assertEquals(0.25, builder.getAffixChance(), 1e-6);
        assertEquals(3, builder.getMaxAffixes());

        builder.affixes(0.10);
        assertEquals(0.10, builder.getAffixChance(), 1e-6);
        assertEquals(1, builder.getMaxAffixes());

        assertThrows(IllegalArgumentException.class, () -> builder.affixes(-0.1));
        assertThrows(IllegalArgumentException.class, () -> builder.affixes(1.1));
    }

    @Test
    @DisplayName("SpawningBuilder should configure environment conditions")
    void testEnvironmentConditions() {
        SpawningBuilder builder = new SpawningBuilder();
        builder.overworld()
                .nether()
                .theEnd()
                .environment(org.bukkit.World.Environment.CUSTOM);

        assertEquals(4, builder.getConditions().size());
        assertEquals(4, builder.buildProfile().conditions().size());
    }
}
