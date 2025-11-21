package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enum representing types of abilities triggered by player actions or events.
 * Supports differentiation between right-click, left-click, sneak states, and entity hits.
 */
public enum AbilityType {
    RIGHT_CLICK(true, false, false),
    LEFT_CLICK(false, true, false),
    LEFT_OR_RIGHT_CLICK(true, true, false),
    SNEAK(false, false, true),
    SNEAK_RIGHT_CLICK(true, false, true),
    SNEAK_LEFT_CLICK(false, true, true),
    SNEAK_LEFT_OR_RIGHT_CLICK(true, true, true),
    FULL_SET_BONUS(false, false, false),
    ENTITY_HIT(false, false, false),
    PRE_HIT(false, false, false),
    AFTER_HIT(false, false, false);

    private final boolean rightClick, leftClick, sneak;

    AbilityType(boolean rightClick, boolean leftClick, boolean sneak) {
        this.rightClick = rightClick;
        this.leftClick = leftClick;
        this.sneak = sneak;
    }

    public boolean isRightClick() {
        return rightClick;
    }

    public boolean isLeftClick() {
        return leftClick;
    }

    public boolean isSneak() {
        return sneak;
    }

    /**
     * Converts this AbilityType to a set of Bukkit Actions it corresponds to.
     *
     * @return Set of corresponding Actions.
     */
    public Set<Action> toActions() {
        EnumSet<Action> actions = EnumSet.noneOf(Action.class);
        if (rightClick) {
            actions.add(Action.RIGHT_CLICK_AIR);
            actions.add(Action.RIGHT_CLICK_BLOCK);
        }
        if (leftClick) {
            actions.add(Action.LEFT_CLICK_AIR);
            actions.add(Action.LEFT_CLICK_BLOCK);
        }
        return actions;
    }

    /**
     * Checks if this AbilityType matches the given Bukkit Event.
     *
     * @param event the event to check against
     * @return true if this AbilityType matches the event context, false otherwise
     */
    public boolean matches(Event event) {
        if (event instanceof PlayerInteractEvent interactEvent) {
            Action action = interactEvent.getAction();
            boolean isSneaking = interactEvent.getPlayer().isSneaking();

            if (this.isSneak() && !isSneaking) return false;

            if (!this.isSneak() && isSneaking
                    && this != SNEAK
                    && this != SNEAK_RIGHT_CLICK
                    && this != SNEAK_LEFT_CLICK
                    && this != SNEAK_LEFT_OR_RIGHT_CLICK)
                return false;

            if (this.isRightClick() && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK))
                return true;

            if (this.isLeftClick() && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK))
                return true;

            // Special case for SNEAK only (no click)
            if (this == SNEAK && isSneaking
                    && (action == Action.PHYSICAL
                    || action == Action.RIGHT_CLICK_AIR
                    || action == Action.RIGHT_CLICK_BLOCK
                    || action == Action.LEFT_CLICK_AIR
                    || action == Action.LEFT_CLICK_BLOCK))
                return true;

            // Sneak + click combinations
            return this.isSneak() && toActions().contains(action);
        }

        if (event instanceof EntityDamageByEntityEvent) {
            return this == ENTITY_HIT;
        }

        return false;
    }
}
