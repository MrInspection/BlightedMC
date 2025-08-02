package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.EnumSet;
import java.util.Set;

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

  public boolean isRightClick() { return rightClick; }
  public boolean isLeftClick() { return leftClick; }
  public boolean isSneak() { return sneak; }

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

  public boolean matches(org.bukkit.event.Event event) {
      // Handle click abilities
      if (event instanceof PlayerInteractEvent interactEvent) {
          Action action = interactEvent.getAction();
          boolean isSneaking = interactEvent.getPlayer().isSneaking();
          if (this.isSneak() && !isSneaking) return false;
          if (!this.isSneak() && isSneaking && this != SNEAK && this != SNEAK_RIGHT_CLICK && this != SNEAK_LEFT_CLICK && this != SNEAK_LEFT_OR_RIGHT_CLICK) return false;
          if (this.isRightClick() && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return true;
          if (this.isLeftClick() && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) return true;
          // SNEAK only
          if (this == SNEAK && isSneaking && (action == Action.PHYSICAL || action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) return true;
          // SNEAK + click
          if (this.isSneak() && isSneaking && (this.isRightClick() && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) || this.isLeftClick() && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK))) return true;
          return false;
      }
      // Handle entity hit
      if (event instanceof EntityDamageByEntityEvent) {
          return this == ENTITY_HIT;
      }
      // Add more event types as needed
      return false;
  }
}
