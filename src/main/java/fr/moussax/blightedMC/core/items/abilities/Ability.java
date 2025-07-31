package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.Event;

public class Ability {
  private final AbilityManager<? extends Event> abilityManager;
  private final String abilityName;
  private final AbilityType abilityType;
  private final AbilityLore abilityLore;

  private final int manaCost;
  private final int cooldown;
  private boolean isPercentage = false;
  private double percentage;

  public Ability(AbilityManager<? extends Event> abilityManager, String abilityName,
                 AbilityType abilityType, AbilityLore abilityLore, int manaCost, int cooldown) {
    this.abilityManager = abilityManager;
    this.abilityName = abilityName;
    this.abilityType = abilityType;
    this.abilityLore = abilityLore;
    this.manaCost = manaCost;
    this.cooldown = cooldown;
  }

  public AbilityManager<? extends Event> getAbilityManager() {
    return abilityManager;
  }

  public String getAbilityName() {
    return abilityName;
  }

  public AbilityType getAbilityType() {
    return abilityType;
  }

  public AbilityLore getAbilityLore() {
    return abilityLore;
  }

  public int getManaCost() {
    return manaCost;
  }

  public int getCooldown() {
    return cooldown;
  }

  public boolean isPercentage() {
    return isPercentage;
  }

  public double getPercentage() {
    return percentage;
  }

  public void setPercentage(double percentage) {
    isPercentage = true;
    this.percentage = percentage;
  }
}
