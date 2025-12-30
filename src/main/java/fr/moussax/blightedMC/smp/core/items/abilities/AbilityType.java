package fr.moussax.blightedMC.smp.core.items.abilities;

import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public enum AbilityType {
    RIGHT_CLICK("§d§lRIGHT CLICK"),
    LEFT_CLICK("§d§lLEFT CLICK"),
    LEFT_OR_RIGHT_CLICK("§d§lCLICK"),
    SNEAK("§d§lSNEAK"),
    SNEAK_RIGHT_CLICK("§d§lSNEAK + RIGHT CLICK"),
    SNEAK_LEFT_CLICK("§d§lSNEAK + LEFT CLICK"),
    SNEAK_LEFT_OR_RIGHT_CLICK("§d§lSNEAK + CLICK"),
    FULL_SET_BONUS("§6§lFULL SET BONUS"),
    ENTITY_HIT("§d§lON HIT"),
    PRE_HIT("§d§lPRE HIT"),
    AFTER_HIT("§d§lAFTER HIT");

    private final String displayName;

    AbilityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean matches(Event event) {
        if (event instanceof PlayerInteractEvent ie) {
            Action action = ie.getAction();
            boolean isSneaking = ie.getPlayer().isSneaking();
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
        return false;
    }
}
