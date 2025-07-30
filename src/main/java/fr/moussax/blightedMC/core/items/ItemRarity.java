package fr.moussax.blightedMC.core.items;

public enum ItemRarity {
  COMMON("§f§lCOMMON", "§f"),
  UNCOMMON("§e§lUNCOMMON", "§e"),
  RARE("§b§lRARE", "§b"),
  EPIC("§d§lEPIC", "§d"),
  LEGENDARY("§c§lLEGENDARY", "§c");

  private final String name;
  private final String colorPrefix;

  ItemRarity(String name, String colorPrefix) {
    this.name = name;
    this.colorPrefix = colorPrefix;
  }

  public String getColorPrefix() {
    return colorPrefix;
  }
}
