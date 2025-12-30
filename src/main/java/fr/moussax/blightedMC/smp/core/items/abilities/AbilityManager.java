package fr.moussax.blightedMC.smp.core.items.abilities;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.event.Event;

/**
 * Defines the execution logic and lifecycle hooks for an {@link Ability}.
 *
 * @param <T> the {@link Event} that can trigger this ability
 */
public interface AbilityManager<T extends Event> {

    /**
     * Executes the ability logic.
     *
     * @return {@code true} if execution succeeded, {@code false} otherwise
     */
    boolean triggerAbility(T event);

    /** @return cooldown duration in seconds */
    int getCooldownSeconds();

    /** @return mana cost required to activate the ability */
    int getManaCost();

    /** @return whether the ability can currently be triggered */
    boolean canTrigger(BlightedPlayer player);

    /** Called after a successful activation */
    void start(BlightedPlayer player);

    /** Stops and cleans up the ability */
    void stop(BlightedPlayer player);

    /** @return ability description lines */
    default String[] getDescription() {
        return new String[0];
    }
}
