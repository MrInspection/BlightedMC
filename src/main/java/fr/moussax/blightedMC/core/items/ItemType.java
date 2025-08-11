package fr.moussax.blightedMC.core.items;

public enum ItemType {
  HELMET(Category.ARMOR),
  CHESTPLATE(Category.ARMOR),
  LEGGINGS(Category.ARMOR),
  BOOTS(Category.ARMOR),

  SWORD(Category.MELEE_WEAPON),
  LONGSWORD(Category.MELEE_WEAPON),
  WAND(Category.MELEE_WEAPON),

  BOW(Category.RANGE_WEAPON),

  PICKAXE(Category.TOOLS),
  DRILL(Category.TOOLS),
  AXE(Category.TOOLS),
  HOE(Category.TOOLS),
  SHOVEL(Category.TOOLS),
  FISHING_ROD(Category.TOOLS),

  MATERIAL(Category.MATERIAL),
  GEMSTONE(Category.MATERIAL),
  UPGRADE_MODULE(Category.MATERIAL),
  BLOCK(Category.BLOCKS),

  UNCATEGORIZED(Category.MISCELLANEOUS),
  ENCHANTED_BOOK(Category.MISCELLANEOUS);

  private final Category category;

  ItemType(Category category) {
    this.category = category;
  }

  public Category getCategory() {
    return category;
  }

  public enum Category {
    ARMOR,
    MELEE_WEAPON,
    RANGE_WEAPON,
    TOOLS,
    BLOCKS,
    MATERIAL,
    MISCELLANEOUS
  }
}
