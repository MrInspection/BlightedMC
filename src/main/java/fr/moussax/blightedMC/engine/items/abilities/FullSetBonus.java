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

public interface FullSetBonus {

    void startAbilityEffect();
    void stopAbilityEffect();
    int getPieces();
    int getMaxPieces();
    void setPlayer(BlightedPlayer player);

    String getName();
    String[] getDescription();

    /** @return Defines how the lore label is formatted. */
    default BonusCategory getCategory() {
        return getMaxPieces() > 1 ? BonusCategory.FULL_SET : BonusCategory.PIECE;
    }

    default SetType getType() {
        return SetType.NORMAL;
    }

    default boolean hasListener() {
        return this instanceof Listener;
    }

    default void activate() {
        if (hasListener()) {
            Bukkit.getPluginManager().registerEvents(
                    (Listener) this,
                    BlightedMC.getInstance()
            );
        }
        startAbilityEffect();
    }

    default void deactivate() {
        stopAbilityEffect();
        if (hasListener()) {
            HandlerList.unregisterAll((Listener) this);
        }
    }

    default FullSetBonus createNew(BlightedPlayer player) {
        try {
            FullSetBonus clone = this.getClass().getDeclaredConstructor().newInstance();
            clone.setPlayer(player);
            return clone;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate FullSetBonus", e);
        }
    }

    default boolean isAbilityOwner(Player eventPlayer) {
        BlightedPlayer owner = getAbilityOwner();
        return owner != null && eventPlayer.getUniqueId().equals(owner.getPlayer().getUniqueId());
    }

    default BlightedPlayer getAbilityOwner() {
        return null;
    }

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

    enum SetType { NORMAL, SNEAK }

    @Getter
    enum BonusCategory {
        FULL_SET("Full Set Bonus"),
        PIECE("Piece Bonus"),
        PASSIVE("Passive"),
        ABILITY("Ability");

        private final String label;

        BonusCategory(String label) {
            this.label = label;
        }
    }
}
