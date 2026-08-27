package fr.moussax.blightedMC.engine.items.abilities;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a set bonus or piece bonus contract activated by wearing matching custom armor.
 */
public interface FullSetBonus {

    /** Activates passive or periodic ability effects for the set bonus. */
    void startAbilityEffect();

    /** Deactivates passive or periodic ability effects for the set bonus. */
    void stopAbilityEffect();

    /**
     * Gets the current number of equipped armor pieces matching this set bonus.
     *
     * @return equipped piece count
     */
    int getPieces();

    /**
     * Gets the total number of armor pieces required to trigger this bonus.
     *
     * @return required piece count
     */
    int getMaxPieces();

    /**
     * Sets the player context bound to this set bonus instance.
     *
     * @param player player context to bind
     */
    void setPlayer(BlightedPlayer player);

    /**
     * Gets the display name of this set bonus.
     *
     * @return display name
     */
    String getName();

    /**
     * Gets description lines describing the bonus effect.
     *
     * @return description lines
     */
    String[] getDescription();

    /**
     * Defines how the lore label is formatted based on piece requirements.
     *
     * @return category designation
     */
    default BonusCategory getCategory() {
        return getMaxPieces() > 1 ? BonusCategory.FULL_SET : BonusCategory.PIECE;
    }

    /**
     * Gets the trigger condition type for this set bonus.
     *
     * @return set bonus type
     */
    default SetType getType() {
        return SetType.NORMAL;
    }

    /**
     * Checks whether this set bonus implements {@link Listener} for Bukkit events.
     *
     * @return {@code true} if this instance is a Bukkit listener, {@code false} otherwise
     */
    default boolean hasListener() {
        return this instanceof Listener;
    }

    /**
     * Registers event listeners if applicable and starts ability effects.
     */
    default void activate() {
        if (hasListener()) {
            Bukkit.getPluginManager().registerEvents(
                    (Listener) this,
                    BlightedMC.getInstance()
            );
        }
        startAbilityEffect();
    }

    /**
     * Stops ability effects and unregisters event listeners if applicable.
     */
    default void deactivate() {
        stopAbilityEffect();
        if (hasListener()) {
            HandlerList.unregisterAll((Listener) this);
        }
    }

    /**
     * Creates a new instance of this set bonus bound to the specified player.
     *
     * @param player player context for the new instance
     * @return new set bonus instance
     */
    default FullSetBonus createNew(BlightedPlayer player) {
        try {
            FullSetBonus clone = this.getClass().getDeclaredConstructor().newInstance();
            clone.setPlayer(player);
            return clone;
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot instantiate FullSetBonus", exception);
        }
    }

    /**
     * Checks if the specified player is the owner of this set bonus instance.
     *
     * @param eventPlayer player to check
     * @return {@code true} if the player owns this ability, {@code false} otherwise
     */
    default boolean isAbilityOwner(Player eventPlayer) {
        BlightedPlayer owner = getAbilityOwner();
        return owner != null && eventPlayer.getUniqueId().equals(owner.getPlayer().getUniqueId());
    }

    /**
     * Gets the player owner bound to this set bonus instance.
     *
     * @return player context owner, or {@code null} if unassigned
     */
    default BlightedPlayer getAbilityOwner() {
        return null;
    }

    /**
     * Formats and returns lore lines describing this set bonus for item tooltips.
     *
     * @return formatted lore lines
     */
    default List<String> getBonusLore() {
        List<String> lore = new ArrayList<>();

        String prefix = getCategory().getLabel();
        if (getType() == SetType.SNEAK) {
            prefix = "Sneak " + prefix;
        }

        lore.add("");
        lore.add(" §5" + prefix + ": " + getName());

        for (String line : getDescription()) {
            lore.add(" §7" + line);
        }
        return lore;
    }

    /** Activation trigger mode for set bonuses. */
    enum SetType {
        /** Always active while equipped. */
        NORMAL,
        /** Active only while sneaking. */
        SNEAK
    }

    /** Display category designation for set bonuses. */
    @Getter
    enum BonusCategory {
        /** Full set bonus requiring multiple pieces. */
        FULL_SET("Full Set Bonus"),
        /** Piece bonus granted by a single armor piece. */
        PIECE("Piece Bonus"),
        /** Passive set effect. */
        PASSIVE("Passive"),
        /** Active set ability. */
        ABILITY("Ability");

        private final String label;

        BonusCategory(String label) {
            this.label = label;
        }
    }
}
