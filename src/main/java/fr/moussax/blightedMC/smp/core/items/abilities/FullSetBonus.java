package fr.moussax.blightedMC.smp.core.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Represents a bonus granted when wearing a complete or partial armor set.
 */
public interface FullSetBonus {

    /** Starts the bonus effect. */
    void startAbilityEffect();

    /** Stops the bonus effect. */
    void stopAbilityEffect();

    /** @return number of equipped pieces contributing to this bonus */
    int getPieces();

    /** @return required number of pieces to activate this bonus */
    int getMaxPieces();

    /** Assigns the owning player. */
    void setPlayer(BlightedPlayer player);

    /** @return activation type of this bonus */
    default SetType getType() {
        return SetType.NORMAL;
    }

    /** @return whether this bonus registers Bukkit listeners */
    default boolean hasListener() {
        return this instanceof Listener;
    }

    /** Activates the bonus and registers listeners if needed. */
    default void activate() {
        if (hasListener()) {
            Bukkit.getPluginManager().registerEvents(
                (Listener) this,
                BlightedMC.getInstance()
            );
        }
        startAbilityEffect();
    }

    /** Deactivates the bonus and unregisters listeners if needed. */
    default void deactivate() {
        stopAbilityEffect();
        if (hasListener()) {
            HandlerList.unregisterAll((Listener) this);
        }
    }

    /** @return a new instance of this bonus bound to the given player */
    default FullSetBonus createNew(BlightedPlayer player) {
        try {
            FullSetBonus clone = this.getClass().getDeclaredConstructor().newInstance();
            clone.setPlayer(player);
            return clone;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate FullSetBonus", e);
        }
    }

    /**
     * Checks whether the given player owns this bonus.
     *
     * @param eventPlayer player from an event
     * @return {@code true} if the player owns this bonus
     */
    default boolean isAbilityOwner(Player eventPlayer) {
        BlightedPlayer owner = getAbilityOwner();
        return owner != null
            && owner.getPlayer() != null
            && eventPlayer.getUniqueId().equals(owner.getPlayer().getUniqueId());
    }

    /** @return owning player of this bonus */
    default BlightedPlayer getAbilityOwner() {
        return null;
    }

    enum SetType { NORMAL, SNEAK }
}
