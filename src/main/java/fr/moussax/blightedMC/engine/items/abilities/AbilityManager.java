package fr.moussax.blightedMC.engine.items.abilities;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.event.Event;

/**
 * Defines the execution logic, lifecycle hooks, and resource requirements for an {@link Ability}.
 *
 * @param <T> event type that triggers this ability
 */
public interface AbilityManager<T extends Event> {

    /**
     * Executes the ability logic when triggered by an event.
     *
     * @param event triggering Bukkit event
     * @return {@code true} if execution succeeded, {@code false} otherwise
     */
    boolean triggerAbility(T event);

    /**
     * Gets the cooldown duration applied after triggering this ability.
     *
     * @return cooldown duration in seconds
     */
    int getCooldownSeconds();

    /**
     * Gets the mana cost required to activate this ability.
     *
     * @return required mana amount
     */
    int getManaCost();

    /**
     * Checks whether the specified player meets the requirements to trigger this ability.
     *
     * @param player player context to evaluate
     * @return {@code true} if the player can activate the ability, {@code false} otherwise
     */
    boolean canTrigger(BlightedPlayer player);

    /**
     * Called immediately after successful ability execution.
     *
     * @param player player context that activated the ability
     */
    void start(BlightedPlayer player);

    /**
     * Stops and cleans up active ability effects or scheduled tasks.
     *
     * @param player player context owning the ability
     */
    void stop(BlightedPlayer player);

    /**
     * Determines whether the triggering event should be cancelled following ability execution.
     *
     * @param success outcome of {@link #triggerAbility(Event)}
     * @return {@code true} to cancel the triggering event, {@code false} to allow vanilla event resolution
     */
    default boolean cancelEvent(boolean success) {
        return true;
    }

    /**
     * Gets description lines formatted for item tooltip lore display.
     *
     * @return array of lore description lines
     */
    default String[] getDescription() {
        return new String[0];
    }
}
