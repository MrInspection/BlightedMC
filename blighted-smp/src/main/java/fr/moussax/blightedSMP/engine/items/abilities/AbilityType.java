package fr.moussax.blightedSMP.engine.items.abilities;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Defines trigger conditions and interaction modes for item abilities.
 */
@Getter
public enum AbilityType {

    /** Ability triggered by right-clicking. */
    RIGHT_CLICK("§d§lRIGHT CLICK"),
    /** Ability triggered by left-clicking. */
    LEFT_CLICK("§d§lLEFT CLICK"),
    /** Ability triggered by either left or right clicking. */
    LEFT_OR_RIGHT_CLICK("§d§lCLICK"),
    /** Ability triggered while sneaking. */
    SNEAK("§d§lSNEAK"),
    /** Ability triggered by sneaking and right-clicking. */
    SNEAK_RIGHT_CLICK("§d§lSNEAK + RIGHT CLICK"),
    /** Ability triggered by sneaking and left-clicking. */
    SNEAK_LEFT_CLICK("§d§lSNEAK + LEFT CLICK"),
    /** Ability triggered by sneaking and clicking. */
    SNEAK_LEFT_OR_RIGHT_CLICK("§d§lSNEAK + CLICK"),
    /** Ability activated when a full armor set is worn. */
    FULL_SET_BONUS("§6§lFULL SET BONUS"),
    /** Ability triggered when hitting an entity. */
    ENTITY_HIT("§d§lON HIT"),
    /** Ability triggered before dealing damage. */
    PRE_HIT("§d§lPRE HIT"),
    /** Ability triggered after dealing damage. */
    AFTER_HIT("§d§lAFTER HIT"),
    /** Passive ability continuously active or triggered on passive events. */
    PASSIVE("");

    private final String displayName;

    AbilityType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Determines whether the given event matches the trigger criteria for this ability type.
     *
     * @param event Bukkit event to evaluate
     * @return {@code true} if the event satisfies this trigger condition, {@code false} otherwise
     */
    public boolean matches(Event event) {
        if (event instanceof PlayerInteractEvent interactEvent) {
            Action action = interactEvent.getAction();
            boolean isSneaking = interactEvent.getPlayer().isSneaking();
            boolean isLeft = (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK);
            boolean isRight = (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);

            return switch (this) {
                case RIGHT_CLICK -> isRight;
                case LEFT_CLICK -> isLeft;
                case LEFT_OR_RIGHT_CLICK -> (isLeft || isRight);
                case SNEAK -> isSneaking;
                case SNEAK_RIGHT_CLICK -> isRight && isSneaking;
                case SNEAK_LEFT_CLICK -> isLeft && isSneaking;
                case SNEAK_LEFT_OR_RIGHT_CLICK -> (isLeft || isRight) && isSneaking;
                default -> false;
            };
        }

        if (event instanceof EntityDamageByEntityEvent) {
            return this == ENTITY_HIT;
        }

        if (event instanceof BlockBreakEvent) {
            return this == PASSIVE;
        }

        return false;
    }
}
