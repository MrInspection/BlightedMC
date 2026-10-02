package fr.moussax.blightedSMP.engine.entities.state;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class EntityCooldownsTest {

    @Test
    @DisplayName("checkAndTrigger should reject subsequent calls within the same millisecond")
    void testCheckAndTriggerSameMillisecond() {
        AtomicLong currentTime = new AtomicLong(1000L);
        EntityCooldowns cooldowns = new EntityCooldowns(currentTime::get);

        assertTrue(cooldowns.checkAndTrigger("slam", 500L), "Initial trigger should be accepted");
        assertFalse(cooldowns.checkAndTrigger("slam", 500L), "Same millisecond trigger must be rejected");

        currentTime.set(1200L);
        assertFalse(cooldowns.checkAndTrigger("slam", 500L), "Trigger before cooldown expires must be rejected");

        currentTime.set(1500L);
        assertTrue(cooldowns.checkAndTrigger("slam", 500L), "Trigger after cooldown expires must be accepted");
        assertFalse(cooldowns.checkAndTrigger("slam", 500L), "Immediate second trigger must be rejected");
    }

    @Test
    @DisplayName("isReady and trigger should manage timestamps accurately")
    void testIsReadyAndTrigger() {
        AtomicLong currentTime = new AtomicLong(1000L);
        EntityCooldowns cooldowns = new EntityCooldowns(currentTime::get);

        assertTrue(cooldowns.isReady("dash", 300L));
        assertEquals(0L, cooldowns.getRemainingMillis("dash", 300L));

        cooldowns.trigger("dash");
        assertFalse(cooldowns.isReady("dash", 300L));
        assertEquals(300L, cooldowns.getRemainingMillis("dash", 300L));

        currentTime.addAndGet(100L);
        assertFalse(cooldowns.isReady("dash", 300L));
        assertEquals(200L, cooldowns.getRemainingMillis("dash", 300L));

        currentTime.addAndGet(200L);
        assertTrue(cooldowns.isReady("dash", 300L));
        assertEquals(0L, cooldowns.getRemainingMillis("dash", 300L));
    }

    @Test
    @DisplayName("reset and resetAll should clear cooldowns")
    void testReset() {
        AtomicLong currentTime = new AtomicLong(1000L);
        EntityCooldowns cooldowns = new EntityCooldowns(currentTime::get);

        cooldowns.trigger("abilityA");
        cooldowns.trigger("abilityB");

        assertFalse(cooldowns.isReady("abilityA", 1000L));
        assertFalse(cooldowns.isReady("abilityB", 1000L));

        cooldowns.reset("abilityA");
        assertTrue(cooldowns.isReady("abilityA", 1000L));
        assertFalse(cooldowns.isReady("abilityB", 1000L));

        cooldowns.resetAll();
        assertTrue(cooldowns.isReady("abilityB", 1000L));
    }

    @Test
    @DisplayName("Null checks throw NullPointerException")
    void testNullChecks() {
        EntityCooldowns cooldowns = new EntityCooldowns();

        assertThrows(NullPointerException.class, () -> cooldowns.checkAndTrigger(null, 100L));
        assertThrows(NullPointerException.class, () -> cooldowns.isReady(null, 100L));
        assertThrows(NullPointerException.class, () -> cooldowns.trigger(null));
        assertThrows(NullPointerException.class, () -> cooldowns.reset(null));
        assertThrows(NullPointerException.class, () -> cooldowns.getRemainingMillis(null, 100L));
    }
}
