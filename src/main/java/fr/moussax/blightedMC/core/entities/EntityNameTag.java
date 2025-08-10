package fr.moussax.blightedMC.core.entities;

public enum EntityNameTag {
  HIDDEN,
  DEFAULT,
  BLIGHTED,
  SMALL_NUMBER,
  BOSS;

  /**
   * Formats the display name based on the selected name tag style.
   *
   * @param name       base entity name
   * @param health     current health
   * @param maxHealth  maximum health
   * @return formatted display string (may be empty for HIDDEN)
   */
  public String format(String name, double health, int maxHealth) {
    double percentage = (maxHealth > 0) ? (health / maxHealth) * 100.0 : 0.0;
    String colorPrefix = percentage < 10 ? "§c" : (percentage < 50 ? "§e" : "§a");

    return switch (this) {
      case HIDDEN -> "";
      case BOSS -> "§d﴾ §5" + name + " " + colorPrefix + toShortNumber(health) + "§c❤ §d﴿";
      case BLIGHTED -> "§5Blighted " + name + " §d" + (int) health + "§r/§5" + maxHealth + "§c❤";
      case SMALL_NUMBER -> "§c" + name + " " + colorPrefix + toShortNumber(health) + "§c❤";
      case DEFAULT -> "§c" + name + " " + colorPrefix + (int) health + "§8/§a" + maxHealth + "§c❤";
    };
  }

  private static String toShortNumber(double value) {
    if (value >= 1_000_000_000) return String.format("%.1fB", value / 1_000_000_000);
    if (value >= 1_000_000) return String.format("%.1fM", value / 1_000_000);
    if (value >= 1_000) return String.format("%.1fK", value / 1_000);
    return String.valueOf((int) value);
  }
}
