package fr.moussax.blightedMC.core.entities.LootTable;

import fr.moussax.blightedMC.core.entities.LootTable.favors.FavorsItem;
import fr.moussax.blightedMC.core.entities.LootTable.favors.FavorsLoot;
import fr.moussax.blightedMC.core.entities.LootTable.favors.FavorsLootAdapter;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootTable {
  private final List<LootEntry> lootEntries = new ArrayList<>();
  private static final Random randomizer = new Random();

  public LootTable addLoot(String itemId, int minAmount, int maxAmount, double dropChance, LootDropRarity rarity) {
    ItemLoot itemLoot = new ItemLoot(
        ItemsRegistry.BLIGHTED_ITEMS.get(itemId).toItemStack(),
        minAmount, maxAmount
    );
    lootEntries.add(new LootEntry(itemLoot, dropChance, rarity));
    return this;
  }

  public LootTable addLoot(Material material, int minAmount, int maxAmount, double dropChance, LootDropRarity rarity) {
    ItemStack stack = new ItemStack(material);
    ItemLoot itemLoot = new ItemLoot(stack, minAmount, maxAmount);
    lootEntries.add(new LootEntry(itemLoot, dropChance, rarity));
    return this;
  }

  public LootTable addFavorsLoot(int favors, double dropChance, LootDropRarity rarity) {
    FavorsLoot favorsLoot = new FavorsLoot(favors);
    FavorsLootAdapter adapter = new FavorsLootAdapter(favorsLoot, new FavorsItem(favors).createItemStack());
    lootEntries.add(new LootEntry(adapter, dropChance, rarity));
    return this;
  }

  public void dropLoot(Location location, BlightedPlayer killer) {
    for (LootEntry entry : lootEntries) {
      if (randomizer.nextDouble() <= entry.dropChance) {
        int amount = entry.item.generateAmount();
        String itemName = formatItemName(entry.item.name(), amount);
        entry.item.consume(killer, location, false, amount);
        if (killer != null && shouldSendMessage(entry.rarity)) {
          killer.getPlayer().sendMessage(getRarityMessage(entry.rarity, itemName));
          playDropSound(killer, entry.rarity);
        }
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

  private record LootEntry(ItemLoot item, double dropChance, LootDropRarity rarity) {}
}
