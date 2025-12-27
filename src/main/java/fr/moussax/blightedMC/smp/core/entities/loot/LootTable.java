package fr.moussax.blightedMC.smp.core.entities.loot;

import fr.moussax.blightedMC.smp.core.entities.loot.gems.GemsItem;
import fr.moussax.blightedMC.smp.core.entities.loot.gems.GemsLoot;
import fr.moussax.blightedMC.smp.core.entities.loot.gems.GemsLootAdapter;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
        ItemLoot itemLoot = new ItemLoot(Objects.requireNonNull(ItemRegistry.getItem(itemId)).toItemStack(), min, max);
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
     * Adds a specific ItemStack to the loot table.
     *
     * @param item       the ItemStack to drop
     * @param min        minimum drop quantity
     * @param max        maximum drop quantity
     * @param dropChance probability to drop (0.0-1.0)
     * @param rarity     the drop rarity
     * @return this LootTable for chaining
     */
    public LootTable addLoot(ItemStack item, int min, int max, double dropChance, LootDropRarity rarity) {
        ItemLoot itemLoot = new ItemLoot(item, min, max);
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
        GemsLoot gemsLoot = new GemsLoot(gems);
        GemsLootAdapter adapter = new GemsLootAdapter(gemsLoot, new GemsItem(gems).createItemStack());
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

        for (LootEntry entry : lootEntries) {
            if (randomizer.nextDouble() <= entry.dropChance) {
                successfulDrops.add(entry.item);
            }
        }

        if (successfulDrops.size() > maxDrops) {
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
        int lootingLevel = 0;

        if (killer != null && killer.getPlayer() != null) {
            ItemStack weapon = killer.getPlayer().getInventory().getItemInMainHand();
            if (weapon.hasItemMeta() && Objects.requireNonNull(weapon.getItemMeta()).hasEnchant(Enchantment.LOOTING)) {
                lootingLevel = weapon.getItemMeta().getEnchantLevel(Enchantment.LOOTING);
            }
        }

        List<LootEntry> successfulDrops = new ArrayList<>();

        for (LootEntry entry : lootEntries) {
            double adjustedChance = entry.rarity.applyLooting(entry.dropChance, lootingLevel);
            if (randomizer.nextDouble() <= adjustedChance) {
                successfulDrops.add(entry);
            }
        }

        if (successfulDrops.size() > maxDrops) {
            Collections.shuffle(successfulDrops, randomizer);
            successfulDrops = successfulDrops.subList(0, maxDrops);
        }

        for (LootEntry entry : successfulDrops) {
            int amount = entry.item.generateAmount();
            String itemName = formatItemName(entry.item.name(), amount);

            entry.item.consume(killer, location, false, amount);

            if (killer != null && killer.getPlayer() != null) {
                handleLootFeedback(killer, entry.rarity, itemName);
            }
        }
    }

    private void handleLootFeedback(BlightedPlayer killer, LootDropRarity rarity, String itemName) {
        String prefix = null;
        Sound sound = null;
        float pitch = 1.0f;

        switch (rarity) {
            case INSANE -> {
                prefix = " §c§lINSANE DROP! §f| §7You found §f";
                sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
                pitch = 0.8f;
            }
            case CRAZY -> {
                prefix = " §d§lCRAZY DROP! §f| §7You found §f";
                sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
                pitch = 1.2f;
            }
            case VERY_RARE -> {
                prefix = " §b§lVERY RARE DROP! §f| §7You found §f";
                sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
                pitch = 1.5f;
            }
            case RARE -> {
                prefix = " §f§lRARE DROP! §f| §7You found §f";
                sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
                pitch = 1.8f;
            }
            default -> {
                // No feedback for COMMON and UNCOMMON items
            }
        }

        if (prefix != null) {
            killer.getPlayer().sendMessage(prefix + itemName);
        }

        if (sound != null) {
            killer.getPlayer().playSound(killer.getPlayer().getLocation(), sound, 1.0f, pitch);
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
    private record LootEntry(ItemLoot item, double dropChance, LootDropRarity rarity) {
    }
}
