package fr.moussax.blightedMC.core.entities.loot;

import fr.moussax.blightedMC.core.entities.loot.gems.GemsItem;
import fr.moussax.blightedMC.core.entities.loot.gems.GemsLoot;
import fr.moussax.blightedMC.core.entities.loot.gems.GemsLootAdapter;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents a loot table capable of generating item drops for entities.
 * Supports standard items, materials, and special Blighted Favors.
 * Handles drop chance, rarity messaging, sound feedback, and maximum drop limits.
 */
public class LootTable {
  private final List<LootEntry> lootEntries = new ArrayList<>();
  private static final Random randomizer = new Random();
  private int maxDrops = 3;

  /**
   * Adds a custom item from the ItemsRegistry to the loot table.
   *
   * @param itemId     the ID of the registered blighted item
   * @param min        minimum drop quantity
   * @param max        maximum drop quantity
   * @param dropChance probability to drop (0.0-1.0)
   * @param rarity     the drop rarity
   * @return this LootTable for chaining
   */
  public LootTable addLoot(String itemId, int min, int max, double dropChance, LootDropRarity rarity) {
    ItemLoot itemLoot = new ItemLoot(ItemDirectory.getItem(itemId).toItemStack(), min, max);
    lootEntries.add(new LootEntry(itemLoot, dropChance, rarity));
    return this;
  }

  /**
   * Adds a vanilla Material item to the loot table.
   *
   * @param material   the Material to drop
   * @param min        minimum drop quantity
   * @param max        maximum drop quantity
   * @param dropChance probability to drop (0.0-1.0)
   * @param rarity     the drop rarity
   * @return this LootTable for chaining
   */
  public LootTable addLoot(Material material, int min, int max, double dropChance, LootDropRarity rarity) {
    ItemStack stack = new ItemStack(material);
    ItemLoot itemLoot = new ItemLoot(stack, min, max);
    lootEntries.add(new LootEntry(itemLoot, dropChance, rarity));
    return this;
  }

  /**
   * Adds a Blighted Gemstone loot entry to the loot table.
   *
   * @param gems       number of favors contained
   * @param dropChance probability to drop (0.0-1.0)
   * @param rarity     the drop rarity
   * @return this LootTable for chaining
   */
  public LootTable addGemsLoot(int gems, double dropChance, LootDropRarity rarity) {
    GemsLoot favorsLoot = new GemsLoot(gems);
    GemsLootAdapter adapter = new GemsLootAdapter(favorsLoot, new GemsItem(gems).createItemStack());
    lootEntries.add(new LootEntry(adapter, dropChance, rarity));
    return this;
  }

  /**
   * Sets the maximum number of items that can drop from this table.
   *
   * @param maxDrops the max number of items to drop
   * @return this LootTable for chaining
   */
  public LootTable setMaxDrop(int maxDrops) {
    this.maxDrops = maxDrops;
    return this;
  }

  public int getMaxDrops() {
    return maxDrops;
  }

  /**
   * Generates a list of items that will be given to the player based on drop chances.
   * This method determines which items will drop but doesn't drop them.
   *
   * @return list of DroppableConsumable items to give
   */
  public List<DroppableConsumable> generateLoot() {
    List<DroppableConsumable> successfulDrops = new ArrayList<>();
    
    // First pass: determine which items will drop based on chance
    for (LootEntry entry : lootEntries) {
      if (randomizer.nextDouble() <= entry.dropChance) {
        successfulDrops.add(entry.item);
      }
    }
    
    // Apply max drops limit
    if (successfulDrops.size() > maxDrops) {
      // Shuffle and take only the first maxDrops items
      Collections.shuffle(successfulDrops, randomizer);
      successfulDrops = successfulDrops.subList(0, maxDrops);
    }
    
    return successfulDrops;
  }

  /**
   * Attempts to drop items at the specified location.
   * Determines drops by chance, applies the maximum drop limit,
   * notifies the killer with messages and sounds for rare drops.
   *
   * @param location the world location to drop items
   * @param killer   the player who killed the entity (can be null)
   */
  public void dropLoot(Location location, BlightedPlayer killer) {
    List<LootEntry> successfulDrops = new ArrayList<>();
    
    // First pass: determine which items will drop
    for (LootEntry entry : lootEntries) {
      if (randomizer.nextDouble() <= entry.dropChance) {
        successfulDrops.add(entry);
      }
    }
    
    // Apply max drops limit
    if (successfulDrops.size() > maxDrops) {
      // Shuffle and take only the first maxDrops items
      Collections.shuffle(successfulDrops, randomizer);
      successfulDrops = successfulDrops.subList(0, maxDrops);
    }
    
    // Second pass: actually drop the items
    for (LootEntry entry : successfulDrops) {
      int amount = entry.item.generateAmount();
      String itemName = formatItemName(entry.item.name(), amount);
      entry.item.consume(killer, location, false, amount);
      if (killer != null && shouldSendMessage(entry.rarity)) {
        killer.getPlayer().sendMessage(getRarityMessage(entry.rarity, itemName));
        playDropSound(killer, entry.rarity);
      }
    }
  }

  private boolean shouldSendMessage(LootDropRarity rarity) {
    return switch (rarity) {
      case EXTRAORDINARY, MIRACULOUS, INSANE -> true;
      default -> false;
    };
  }

  private String getRarityMessage(LootDropRarity rarity, String itemName) {
    String prefix = switch (rarity) {
      case INSANE -> " §c§lINSANE DROP! §7You found §c";
      case MIRACULOUS -> " §d§lMIRACULOUS DROP! §7You found §d";
      case EXTRAORDINARY -> " §b§lEXTRAORDINARY DROP! §7You found §5";
      case RARE -> " §e§lRARE DROP! §7You found §b";
      default -> null;
    };
    return prefix + itemName;
  }

  private void playDropSound(BlightedPlayer player, LootDropRarity rarity) {
    Sound sound;
    float pitch;

    switch (rarity) {
      case INSANE -> {
        sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        pitch = 0.9f;
      }
      case MIRACULOUS -> {
        sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        pitch = 1.2f;
      }
      case EXTRAORDINARY -> {
        sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        pitch = 1.5f;
      }
      default -> {
        sound = null;
        pitch = 1.0f;
      }
    }

    if (sound != null && player.getPlayer() != null) {
      player.getPlayer().playSound(player.getPlayer().getLocation(), sound, 1.0f, pitch);
    }
  }

  private String formatItemName(String baseName, int amount) {
    return amount > 1 ? baseName + " §8(x" + amount + ")" : baseName;
  }

  /**
   * Represents a single loot entry containing an item,
   * its drop chance, and rarity.
   *
   * @param item       the lootable item
   * @param dropChance the probability to drop (0.0-1.0)
   * @param rarity     the drop rarity
   */
  private record LootEntry(ItemLoot item, double dropChance, LootDropRarity rarity) { }
}
