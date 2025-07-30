package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.block.Action;

import java.util.HashSet;
import java.util.Set;

public enum AbilityType {
  RIGHT_CLICK(true, false, false, false),
  LEFT_CLICK(false, true, false, false),
  LEFT_OR_RIGHT_CLICK(true, true, false, false),
  SNEAK(false, false, true, false),
  SNEAK_RIGHT_CLICK(true, false, true, false),
  SNEAK_LEFT_CLICK(false, true, true, false),
  SNEAK_LEFT_OR_RIGHT_CLICK(true, true, true, false),
  FULL_SET_BONUS(),
  ENTITY_HIT(),
  PRE_HIT(),
  AFTER_HIT(),;

  private final boolean isRightClick;
  private final boolean isLeftClick;
  private final boolean isSneak;
  private final boolean isOther;

  AbilityType(boolean isRightClick, boolean isLeftClick, boolean isSneak, boolean isOther) {
    this.isRightClick = isRightClick;
    this.isLeftClick = isLeftClick;
    this.isSneak = isSneak;
    this.isOther = isOther;
  }

  AbilityType() {
    this(false, false, false, true);
  }

  @Override
  public String toString() {
    return switch (this) {
      case ENTITY_HIT, PRE_HIT, AFTER_HIT -> "Hit";
      case RIGHT_CLICK -> "Right Click";
      case LEFT_CLICK -> "Left Click";
      case LEFT_OR_RIGHT_CLICK -> "Left/Right Click";
      case SNEAK -> "Sneak";
      case SNEAK_RIGHT_CLICK -> "Sneak Right Click";
      case SNEAK_LEFT_CLICK -> "Sneak Left Click";
      case SNEAK_LEFT_OR_RIGHT_CLICK -> "Sneak Left/Right Click";
      case FULL_SET_BONUS -> "Full Set Bonus";
    };
  }

  public Set<Action> toAction() {
    Set<Action> actions = new HashSet<>();
    if(isRightClick) {
      actions.add(Action.RIGHT_CLICK_AIR);
      actions.add(Action.RIGHT_CLICK_BLOCK);
    }
    if(isLeftClick) {
      actions.add(Action.LEFT_CLICK_AIR);
      actions.add(Action.LEFT_CLICK_BLOCK);
    }
    return actions;
  }

  public boolean isRightClick() {
    return isRightClick;
  }

  public boolean isLeftClick() {
    return isLeftClick;
  }

  public boolean isSneak() {
    return isSneak;
  }

  public boolean isOther() {
    return isOther;
  }
}
