package fr.moussax.blightedMC.core.entities.LootTable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class LootTable {
  private static final Random randomizer = new Random();
  private final Map<ItemStack, LootDropRarity> lootPool = new HashMap<>();

  private ItemStack item;
  private int min = 1, max = 1;
  private double lootChance;
  private LootDropRarity rarity;

  public LootTable(ItemStack item, double lootChance, LootDropRarity rarity) {
    validateChance(lootChance);
    this.item = item;
    this.lootChance = lootChance;
    this.rarity = rarity;
  }

  public LootTable(ItemStack item, int min, int max, double lootChance, LootDropRarity rarity) {
    validateChance(lootChance);
    this.item = item;
    this.min = min;
    this.max = max;
    this.lootChance = lootChance;
    this.rarity = rarity;
  }

  public LootTable addLoot(ItemStack item, LootDropRarity rarity) {
    lootPool.put(item, rarity);
    return this;
  }

  public void dropLootItem(Location location, Player player) {
    if (randomizer.nextDouble() > lootChance) return;

    int amount = randomizer.nextInt(max - min + 1) + min;
    if (amount == 0) return;

    ItemStack clonedItem = item.clone();
    clonedItem.setAmount(amount);
    Objects.requireNonNull(location.getWorld()).dropItemNaturally(location, clonedItem);

    if (player != null && shouldSendMessage(rarity)) {
      player.sendMessage(getRarityMessage(rarity, clonedItem));
    }
  }

  public void dropFromLootPool(Location location, Player player) {
    for (Map.Entry<ItemStack, LootDropRarity> entry : lootPool.entrySet()) {
      ItemStack lootItem = entry.getKey();
      LootDropRarity dropRarity = entry.getValue();

      double dropChance = getChanceByRarity(dropRarity);
      if (randomizer.nextDouble() > dropChance) continue;

      ItemStack cloned = lootItem.clone();
      Objects.requireNonNull(location.getWorld()).dropItemNaturally(location, cloned);

      if (player != null && shouldSendMessage(dropRarity)) {
        player.sendMessage(getRarityMessage(dropRarity, cloned));
      }

      break;
    }
  }

  private void validateChance(double chance) {
    if (chance < 0.0 || chance > 1.0) {
      throw new IllegalArgumentException("lootChance must be between 0.0 and 1.0, got " + chance);
    }
  }

  private boolean shouldSendMessage(LootDropRarity rarity) {
    return rarity.ordinal() <= LootDropRarity.EXTRAORDINARY.ordinal();
  }

  private double getChanceByRarity(LootDropRarity rarity) {
    return switch (rarity) {
      case INSANE -> 0.0003;
      case MIRACULOUS -> 0.006;
      case EXTRAORDINARY -> 0.03;
      case RARE -> 0.11;
      case UNCOMMON -> 0.31;
      case COMMON -> 1.0;
    };
  }

  private String getRarityMessage(LootDropRarity rarity, ItemStack item) {
    String itemName = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()
        ? item.getItemMeta().getDisplayName()
        : item.getType().name();

    return switch (rarity) {
      case INSANE -> "§d§lINSANE DROP! §7You found §r" + itemName;
      case MIRACULOUS -> "§6§lMIRACULOUS DROP! §7You found §r" + itemName;
      case EXTRAORDINARY -> "§5§lEXTRAORDINARY DROP! §7You found §r" + itemName;
      case RARE -> "§9§lRARE DROP! §7You found §r" + itemName;
      case UNCOMMON -> "§a§lUNCOMMON DROP! §7You found §r" + itemName;
      case COMMON -> "§7You found " + itemName;
    };
  }
}
