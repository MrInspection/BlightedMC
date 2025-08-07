package fr.moussax.blightedMC.core.registry.items;

import fr.moussax.blightedMC.core.items.*;
import fr.moussax.blightedMC.core.items.rules.PreventConsumeRule;
import fr.moussax.blightedMC.core.items.rules.PreventPlacementRule;
import org.bukkit.Material;

import java.util.HashMap;

public class WinterItemsRegistry implements ItemCategory {
  public static final HashMap<ItemGenerator, Double> WHITE_GIFT_LOOT_TABLE = new HashMap<>();

  @Override
  public void registerItems() {
    ItemManager whiteGift = new ItemManager("WHITE_GIFT", ItemType.UNCATEGORIZED, ItemRarity.COMMON, Material.PLAYER_HEAD);
    whiteGift.setDisplayName("White Gift");
    whiteGift.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ3YjM3ZTY3YTg5MTU5YmY0YWNjNGE0NGQ0MzI4ZjRlZmMwMTgxNjA1MTQyMjg4ZTVlZWQxYWI4YWVkOTEzYyJ9fX0=");
    whiteGift.addLore("§7Click to a random stranger while holding to gift!", "Both players get the rewards!", "", ItemRarity.COMMON.getName());
    whiteGift.addRule(new PreventPlacementRule());
    ItemsRegistry.addItem(whiteGift);

    ItemManager giftTheFish = new ItemManager("GIFT_THE_FISH", ItemType.UNCATEGORIZED, ItemRarity.SPECIAL, Material.COD);
    giftTheFish.setDisplayName("Gift the Fish");
    giftTheFish.addEnchantmentGlint();
    giftTheFish.addLore(ItemRarity.SPECIAL.getName());
    giftTheFish.addRule(new PreventConsumeRule());
    ItemsRegistry.addItem(giftTheFish);

    ItemManager stormTheFish = new ItemManager("STORM_THE_FISH", ItemType.UNCATEGORIZED, ItemRarity.SPECIAL, Material.SALMON);
    stormTheFish.setDisplayName("Gift the Fish");
    stormTheFish.addEnchantmentGlint();
    stormTheFish.addLore(ItemRarity.SPECIAL.getName());
    stormTheFish.addRule(new PreventConsumeRule());
    ItemsRegistry.addItem(stormTheFish);
  }
}
