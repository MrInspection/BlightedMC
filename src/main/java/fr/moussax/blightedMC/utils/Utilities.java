package fr.moussax.blightedMC.utils;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.crafting.CraftingObject;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static fr.moussax.blightedMC.smp.core.items.BlightedItem.BLIGHTED_ID_KEY;

/**
 * Utility class providing general-purpose helper methods for the BlightedMC plugin.
 *
 * <p>Includes methods for delayed task execution using the Bukkit scheduler.</p>
 */
public class Utilities {

    /**
     * Schedules a task to run after a specified number of server ticks.
     *
     * @param runnable the task to execute
     * @param ticks    number of server ticks to wait before execution
     */
    public static void delay(Runnable runnable, int ticks) {
        delay(runnable, (long) ticks);
    }

    /**
     * Schedules a task to run after a specified number of server ticks.
     *
     * @param runnable the task to execute
     * @param ticks    number of server ticks to wait before execution
     */
    public static void delay(Runnable runnable, long ticks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(BlightedMC.getInstance(), ticks);
    }

    /**
     * Resolves the effective identifier of the given item stack.
     *
     * @param item         the item stack whose identifier is resolved
     * @param resolutionId hint used to force or influence identifier resolution
     * @return the resolved item identifier
     */
    public static String resolveItemId(@NonNull ItemStack item, @NonNull String resolutionId) {
        if (resolutionId.startsWith("vanilla:")) {
            return "vanilla:" + item.getType().name();
        }

        var meta = item.getItemMeta();
        if (meta == null) {
            return "vanilla:" + item.getType().name();
        }

        NamespacedKey itemIdKey = BLIGHTED_ID_KEY;
        String customId = meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
        return customId != null ? customId : "vanilla:" + item.getType().name();
    }

    /**
     * Returns a display name for the given crafting ingredient.
     *
     * @param ingredient the ingredient to get the name from
     * @return display the name of the ingredient
     */
    public static String extractIngredientName(CraftingObject ingredient) {
        if (ingredient.isCustom()) {
            return Objects.requireNonNull(ingredient.getManager().toItemStack().getItemMeta()).getDisplayName();
        }
        return "§f" + Formatter.formatEnumName(ingredient.getVanillaItem().getType().name());
    }

    /**
     * Removes the specified amount of the ingredient from the player's inventory.
     *
     * @param player     the player whose inventory to modify
     * @param ingredient the ingredient and amount to consume
     */
    public static void consumeItemsFromInventory(Player player, CraftingObject ingredient) {
        String requiredId = ingredient.getId();
        int remainingToRemove = ingredient.getAmount();
        ItemStack[] contents = player.getInventory().getContents();

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack item = contents[slot];
            if (item == null || item.getType() == Material.AIR || remainingToRemove <= 0) continue;

            String currentId = resolveItemId(item, requiredId);
            if (!currentId.equals(requiredId)) continue;

            int amountToDeduct = Math.min(item.getAmount(), remainingToRemove);
            int newAmount = item.getAmount() - amountToDeduct;
            remainingToRemove -= amountToDeduct;

            if (newAmount <= 0) {
                player.getInventory().setItem(slot, null);
            } else {
                item.setAmount(newAmount);
            }
        }
    }

    /**
     * Finds the nearest survival/adventure player to a specific entity within a given range.
     *
     * @param source The entity scanning for players (e.g., the Boss).
     * @param range  The maximum radius to scan.
     * @return The nearest valid player, or null if none are found.
     */
    public static Player getNearestPlayer(@NonNull Entity source, double range) {
        Player nearest = null;
        double nearestDistanceSquared = range * range;

        for (Player player : source.getWorld().getPlayers()) {
            if (player.getGameMode() == org.bukkit.GameMode.SPECTATOR || player.getGameMode() == org.bukkit.GameMode.CREATIVE)
                continue;

            double distanceSquared = player.getLocation().distanceSquared(source.getLocation());
            if (distanceSquared <= nearestDistanceSquared) {
                nearest = player;
                nearestDistanceSquared = distanceSquared;
            }
        }
        return nearest;
    }
}
