package fr.moussax.blightedSMP.engine.loot.results;

import fr.moussax.blightedSMP.engine.items.registry.ItemRegistry;
import fr.moussax.blightedSMP.engine.loot.LootContext;
import fr.moussax.blightedSMP.engine.loot.LootResult;
import fr.moussax.bedrock.text.Formatter;
import fr.moussax.bedrock.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A {@link LootResult} representing an item drop.
 */
public final class ItemResult implements LootResult {

    private final ItemStack itemStack;
    private final BiConsumer<ItemBuilder, ThreadLocalRandom> modifier;

    private ItemResult(ItemStack itemStack, BiConsumer<ItemBuilder, ThreadLocalRandom> modifier) {
        this.itemStack = Objects.requireNonNull(itemStack).clone();
        this.modifier = modifier;
    }

    private ItemResult(ItemStack itemStack, Consumer<ItemBuilder> modifier) {
        this(itemStack, modifier != null ? (builder, _) -> modifier.accept(builder) : null);
    }

    /**
     * Creates an item result from a custom registry item identifier.
     *
     * @param itemId registry ID of the item
     * @return new item result
     */
    public static ItemResult of(String itemId) {
        ItemStack item = Objects.requireNonNull(
                ItemRegistry.getItem(itemId), "Item not found in registry: " + itemId
        ).toItemStack();
        return new ItemResult(item, (Consumer<ItemBuilder>) null);
    }

    /**
     * Creates an item result from a vanilla material.
     *
     * @param material material to drop
     * @return new item result
     */
    public static ItemResult of(Material material) {
        return new ItemResult(new ItemStack(material), (Consumer<ItemBuilder>) null);
    }

    /**
     * Creates an item result from an existing item stack.
     *
     * @param itemStack item stack to drop
     * @return new item result
     */
    public static ItemResult of(ItemStack itemStack) {
        return new ItemResult(itemStack, (Consumer<ItemBuilder>) null);
    }

    /**
     * Creates an item result from a custom registry item with a modifier function.
     *
     * @param itemId   registry ID of the item
     * @param modifier function to modify the item builder
     * @return new item result
     */
    public static ItemResult of(String itemId, Consumer<ItemBuilder> modifier) {
        ItemStack item = Objects.requireNonNull(
                ItemRegistry.getItem(itemId), "Item not found in registry: " + itemId
        ).toItemStack();
        return new ItemResult(item, modifier);
    }

    /**
     * Creates an item result from a vanilla material with a modifier function.
     *
     * @param material material to drop
     * @param modifier function to modify the item builder
     * @return new item result
     */
    public static ItemResult of(Material material, Consumer<ItemBuilder> modifier) {
        return new ItemResult(new ItemStack(material), modifier);
    }

    /**
     * Creates an item result from an item stack with a modifier function.
     *
     * @param itemStack item stack to drop
     * @param modifier  function to modify the item builder
     * @return new item result
     */
    public static ItemResult of(ItemStack itemStack, Consumer<ItemBuilder> modifier) {
        return new ItemResult(itemStack, modifier);
    }

    /**
     * Creates an item result with durability randomized within a percentage range.
     *
     * @param material          material to drop
     * @param minimumPercentage minimum durability percentage
     * @param maximumPercentage maximum durability percentage
     * @return new item result
     */
    public static ItemResult randomDurability(Material material, double minimumPercentage, double maximumPercentage) {
        return new ItemResult(new ItemStack(material), (builder, random) -> {
            double percentage = minimumPercentage + (maximumPercentage - minimumPercentage) * random.nextDouble();
            builder.setDurabilityPercent(percentage);
        });
    }

    /**
     * Creates an item result for an enchanted book selected from an enchantment pool.
     *
     * @param enchantmentPool map of candidate enchantments to levels
     * @return new item result
     */
    public static ItemResult randomEnchantedBook(Map<Enchantment, Integer> enchantmentPool) {
        return new ItemResult(new ItemStack(Material.ENCHANTED_BOOK), (builder, random) -> {
            List<Map.Entry<Enchantment, Integer>> entries = List.copyOf(enchantmentPool.entrySet());
            Map.Entry<Enchantment, Integer> selected = entries.get(random.nextInt(entries.size()));
            builder.enchantedBook().addEnchantment(selected.getKey(), selected.getValue());
        });
    }

    /**
     * Creates an item result for an enchanted book with a random level within a range.
     *
     * @param enchantments list of candidate enchantments
     * @param minimumLevel minimum level
     * @param maximumLevel maximum level
     * @return new item result
     */
    public static ItemResult randomEnchantedBook(List<Enchantment> enchantments, int minimumLevel, int maximumLevel) {
        return new ItemResult(new ItemStack(Material.ENCHANTED_BOOK), (builder, random) -> {
            Enchantment selected = enchantments.get(random.nextInt(enchantments.size()));
            int level = random.nextInt(minimumLevel, maximumLevel + 1);
            builder.enchantedBook().addEnchantment(selected, level);
        });
    }

    /**
     * Drops the item at the loot origin, applying amount, velocity, and modifiers.
     *
     * @param context loot context
     * @param amount  quantity of items to drop
     */
    @Override
    public void execute(LootContext context, int amount) {
        ItemStack drop;

        if (modifier != null) {
            ItemBuilder builder = new ItemBuilder(itemStack);
            modifier.accept(builder, context.random());
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
     * Returns a formatted display name for the item, appending quantity if greater than 1.
     *
     * @param amount quantity of items
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
