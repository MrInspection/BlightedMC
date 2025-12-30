package fr.moussax.blightedMC.smp.core.shared.loot.results;

import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.smp.core.shared.loot.LootContext;
import fr.moussax.blightedMC.smp.core.shared.loot.LootResult;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A {@link LootResult} representing an item drop.
 * Supports vanilla {@link Material}, custom registry items, and dynamic modifiers.
 * Modifiers can adjust durability, enchantments, or other item properties before dropping.
 */
public final class ItemResult implements LootResult {
    private final ItemStack itemStack;
    private final Consumer<ItemBuilder> modifier;

    private ItemResult(ItemStack itemStack, Consumer<ItemBuilder> modifier) {
        this.itemStack = Objects.requireNonNull(itemStack).clone();
        this.modifier = modifier;
    }

    /**
     * Creates an ItemResult from a registered item ID.
     *
     * @param itemId the registry ID of the item
     * @return a new ItemResult
     */
    public static ItemResult of(String itemId) {
        ItemStack item = Objects.requireNonNull(
            ItemRegistry.getItem(itemId), "Item not found in registry: " + itemId
        ).toItemStack();
        return new ItemResult(item, null);
    }

    /**
     * Creates an ItemResult from a Material.
     *
     * @param material the Material to drop
     * @return a new ItemResult
     */
    public static ItemResult of(Material material) {
        return new ItemResult(new ItemStack(material), null);
    }

    /**
     * Creates an ItemResult from an existing ItemStack.
     *
     * @param itemStack the ItemStack to drop
     * @return a new ItemResult
     */
    public static ItemResult of(ItemStack itemStack) {
        return new ItemResult(itemStack, null);
    }

    /**
     * Creates an ItemResult from a registry ID with a modifier to adjust the item.
     *
     * @param itemId   the registry ID
     * @param modifier a function to modify the ItemBuilder
     * @return a new ItemResult
     */
    public static ItemResult of(String itemId, Consumer<ItemBuilder> modifier) {
        ItemStack item = Objects.requireNonNull(
            ItemRegistry.getItem(itemId), "Item not found in registry: " + itemId
        ).toItemStack();
        return new ItemResult(item, modifier);
    }

    /**
     * Creates an ItemResult from a Material with a modifier.
     *
     * @param material the Material
     * @param modifier a function to modify the ItemBuilder
     * @return a new ItemResult
     */
    public static ItemResult of(Material material, Consumer<ItemBuilder> modifier) {
        return new ItemResult(new ItemStack(material), modifier);
    }

    /**
     * Creates an ItemResult from an ItemStack with a modifier.
     *
     * @param itemStack the ItemStack
     * @param modifier  a function to modify the ItemBuilder
     * @return a new ItemResult
     */
    public static ItemResult of(ItemStack itemStack, Consumer<ItemBuilder> modifier) {
        return new ItemResult(itemStack, modifier);
    }

    /**
     * Creates an ItemResult with randomized durability.
     *
     * @param material   the Material
     * @param minPercent minimum durability percentage
     * @param maxPercent maximum durability percentage
     * @return a new ItemResult
     */
    public static ItemResult randomDurability(Material material, double minPercent, double maxPercent) {
        return new ItemResult(new ItemStack(material), builder -> {
            double percent = minPercent + (maxPercent - minPercent) * Math.random();
            builder.setDurabilityPercent(percent);
        });
    }

    /**
     * Creates an ItemResult as an enchanted book from a pool of enchantments.
     *
     * @param enchantmentPool mapping of enchantments to levels
     * @return a new ItemResult
     */
    public static ItemResult randomEnchantedBook(Map<Enchantment, Integer> enchantmentPool) {
        return new ItemResult(new ItemStack(Material.ENCHANTED_BOOK), builder -> {
            List<Map.Entry<Enchantment, Integer>> entries = List.copyOf(enchantmentPool.entrySet());
            Map.Entry<Enchantment, Integer> selected = entries.get((int) (Math.random() * entries.size()));
            builder.asEnchantedBook().addEnchantment(selected.getKey(), selected.getValue());
        });
    }

    /**
     * Creates an ItemResult as an enchanted book from a list of enchantments with random levels.
     *
     * @param enchantments list of enchantments
     * @param minLevel     minimum level
     * @param maxLevel     maximum level
     * @return a new ItemResult
     */
    public static ItemResult randomEnchantedBook(List<Enchantment> enchantments, int minLevel, int maxLevel) {
        return new ItemResult(new ItemStack(Material.ENCHANTED_BOOK), builder -> {
            Enchantment selected = enchantments.get((int) (Math.random() * enchantments.size()));
            int level = minLevel + (int) (Math.random() * (maxLevel - minLevel + 1));
            builder.asEnchantedBook().addEnchantment(selected, level);
        });
    }

    /**
     * Drops the item at the loot origin and applies the modifier if present.
     *
     * @param context the loot context
     * @param amount  the number of items to drop
     */
    @Override
    public void execute(LootContext context, int amount) {
        ItemStack drop;

        if (modifier != null) {
            ItemBuilder builder = new ItemBuilder(itemStack);
            modifier.accept(builder);
            drop = builder.toItemStack();
        } else {
            drop = itemStack.clone();
        }

        drop.setAmount(amount);

        Item droppedItem = Objects.requireNonNull(context.origin().getWorld())
            .dropItem(context.origin(), drop);

        if (context.velocity() != null) {
            droppedItem.setVelocity(context.velocity());
        }
    }

    /**
     * Returns a formatted display name for the item, including amount if >1.
     *
     * @param amount the number of items
     * @return formatted display name
     */
    @Override
    public String displayName(int amount) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return formatAmount(meta.getDisplayName(), amount);
        }

        String formattedName = Formatter.formatEnumName(itemStack.getType().name());
        return formatAmount(formattedName, amount);
    }

    private String formatAmount(String name, int amount) {
        return amount > 1 ? name + " §8(x" + amount + ")" : name;
    }
}
