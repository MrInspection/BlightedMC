package fr.moussax.blightedMC.core.items;

import java.util.ArrayList;
import java.util.List;

public enum ItemType {
  HELMET(Type.Armor),
  CHESTPLATE(Type.Armor),
  LEGGINGS(Type.Armor),
  BOOTS(Type.Armor),

  SWORD(Type.Sword),
  LONGSWORD(Type.Sword),
  BOW(Type.Bow),
  WAND(Type.Equipment),

  PICKAXE(Type.Tool),
  DRILL(Type.Tool),
  AXE(Type.Tool),
  HOE(Type.Tool),
  SHOVEL(Type.Tool),
  FISHING_ROD(Type.Tool),

  ENCHANTED_BOOK,
  GEMSTONE,
  MATERIAL,
  UPGRADE_MODULE;

  private final Type type;

  ItemType(Type type) {
    this.type = type;
    type.typeList.add(this);
    Type.available.add(this);
    if (type == Type.Sword || type == Type.Bow) {
      Type.combat.add(this);
    }
  }

  ItemType() {
    this.type = null;
  }

  public Type getType() {
    return type;
  }

  public enum Type {
    Sword,
    Tool,
    Armor,
    Bow,
    Equipment;

    private final List<ItemType> typeList = new ArrayList<>();
    private static final List<ItemType> combat = new ArrayList<>();
    private static final List<ItemType> available = new ArrayList<>();

    public List<ItemType> getTypeList() {
      return typeList;
    }

    public static List<ItemType> getCombat() {
      return combat;
    }

    public static List<ItemType> getAvailable() {
      return available;
    }
  }
}
