package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.block.Action;

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
}
