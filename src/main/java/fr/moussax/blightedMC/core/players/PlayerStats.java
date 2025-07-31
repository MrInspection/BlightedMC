package fr.moussax.blightedMC.core.players;

import org.bukkit.ChatColor;

public enum PlayerStats {
  HEALTH_BONUS("health_bonus", '❤', ChatColor.RED, 0, 10),  // +hearts, max 15 total
  DEFENSE("defense", '❈', ChatColor.GREEN, 0, 50),          // Flat or % reduction
  SPEED_BONUS("speed_bonus", '✦', ChatColor.WHITE, 0, 30),  // +% speed, subtle
  MANA("mana", '✎', ChatColor.AQUA, 100, 100);              // Fixed 100, regen-based

  private final String dataName;
  private final char symbol;
  private final ChatColor color;
  private final double baseValue;
  private final double maxValue;

  PlayerStats(String dataName, char symbol, ChatColor color, double baseValue, double maxValue) {
    this.dataName = dataName;
    this.symbol = symbol;
    this.color = color;
    this.baseValue = baseValue;
    this.maxValue = maxValue;
  }

  public String getDisplayName() {
    return color + "" + symbol + " " + dataName.replace('_', ' ');
  }

  public String getDataName() {
    return dataName;
  }

  public double getBaseValue() {
    return baseValue;
  }

  public double getMaxValue() {
    return maxValue;
  }
}

