package fr.moussax.blightedMC.engine.items;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.items.abilities.Ability;
import fr.moussax.blightedMC.engine.items.abilities.AbilityExecutor;
import fr.moussax.blightedMC.engine.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.engine.items.recipes.RecipePreviewManager;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import fr.moussax.blightedMC.engine.items.rules.ItemRule;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.ui.menu.Menu;
import fr.moussax.blightedMC.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Supplier;

/**
 * Represents a custom BlightedMC item with persistent identity, rarity, abilities,
 * and gameplay rules.
 *
 * <p>This class extends {@link ItemBuilder} to provide fluent item construction
 * while adding custom item system behavior such as ability execution, set bonuses,
 * placement restrictions, interaction rules, and persistent item identification.</p>
 *
 * <p>Built items are identified through {@link #BLIGHTED_ID_KEY} and store their
 * rarity through {@link #BLIGHTED_RARITY_KEY} when {@link #toItemStack()} is called.</p>
 */
public final class BlightedItem extends ItemBuilder implements ItemRule, Supplier<ItemStack> {

    public static final NamespacedKey BLIGHTED_ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_id");
    public static final NamespacedKey BLIGHTED_RARITY_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_rarity");

    @Getter
    private final String itemId;
    @Getter
    private final ItemRarity itemRarity;
    @Getter
    private final ItemType itemType;
    @Getter
    private FullSetBonus fullSetBonus;
    @Getter
    private final List<Ability> abilities = new ArrayList<>();
    private final List<ItemRule> rules = new ArrayList<>();
    /**
     * -- GETTER --
     *
     * @return true if recipe preview is enabled for this item
     */
    @Getter
    private boolean recipePreviewEnabled = false;

    /**
     * Creates a Blighted item from a material.
     *
     * @param itemId   the unique item identifier
     * @param type     the item category
     * @param rarity   the item rarity
     * @param material the base material
     */
    public BlightedItem(@NonNull String itemId, @NonNull ItemType type, @NonNull ItemRarity rarity, @NonNull Material material) {
        super(material);
        this.itemId = itemId;
        this.itemType = type;
        this.itemRarity = rarity;
    }

    /**
     * Creates a Blighted item from an existing item stack.
     *
     * <p>The provided item is copied through {@link ItemBuilder}.</p>
     *
     * @param itemId    the unique item identifier
     * @param type      the item category
     * @param rarity    the item rarity
     * @param itemStack the base item stack
     */
    public BlightedItem(@NonNull String itemId, @NonNull ItemType type, @NonNull ItemRarity rarity, @NonNull ItemStack itemStack) {
        super(itemStack);
        this.itemId = itemId;
        this.itemType = type;
        this.itemRarity = rarity;
    }

    /**
     * Adds an ability to this item and injects its lore representation.
     *
     * @param ability the ability to add
     */
    public void addAbility(Ability ability) {
        addAbility(ability, true);
    }

    /**
     * Adds an ability to this item.
     *
     * @param ability    the ability to add
     * @param injectLore whether the ability description should be added to the item lore
     */
    public void addAbility(Ability ability, boolean injectLore) {
        if (ability == null) return;
        this.abilities.add(ability);

        if (injectLore) {
            List<String> formattedLore = ability.getAbilityLore();
            this.addLore(formattedLore.toArray(new String[0]));
        }
    }

    /**
     * Sets the full set bonus of this item.
     *
     * <p>The bonus description is automatically added to the item lore.</p>
     *
     * @param fullSetBonus the set bonus, or {@code null} to remove it
     */
    public void setFullSetBonus(FullSetBonus fullSetBonus) {
        this.fullSetBonus = fullSetBonus;
        if (fullSetBonus == null) return;

        List<String> formattedLore = fullSetBonus.getBonusLore();
        this.addLore(formattedLore.toArray(new String[0]));
    }

    /**
     * Adds a gameplay rule to this item.
     *
     * @param rule the rule to add
     */
    public void addRule(ItemRule rule) {
        rules.add(rule);
    }

    /**
     * Enables recipe preview support for this item.
     *
     * <p>When enabled, the item can be used to open its associated recipe
     * preview through the recipe preview system.</p>
     */
    public void enableRecipePreview() {
        this.recipePreviewEnabled = true;
    }

    /**
     * Opens this item's recipe preview for a player.
     *
     * <p>If a parent menu is provided, the preview can use it for
     * back-navigation.</p>
     *
     * @param player     the player viewing the recipe preview
     * @param parentMenu the parent menu to return to, or {@code null}
     *                   if no parent menu exists
     * @return {@code true} if the recipe preview was opened successfully;
     * {@code false} otherwise
     */
    public boolean openRecipePreview(@NonNull Player player, @Nullable Menu parentMenu) {
        return RecipePreviewManager.openPreview(player, this, parentMenu);
    }

    /**
     * Opens this item's recipe preview for a player without a parent menu.
     *
     * @param player the player viewing the recipe preview
     * @return {@code true} if the recipe preview was opened successfully;
     * {@code false} otherwise
     */
    public boolean openRecipePreview(@NonNull Player player) {
        return openRecipePreview(player, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BlightedItem setDisplayName(@NonNull String displayName) {
        super.setDisplayName(itemRarity.getColorPrefix() + displayName);
        return this;
    }

    /**
     * Resolves a Blighted item from an item stack.
     *
     * <p>The item stack must contain a valid {@link #BLIGHTED_ID_KEY} value.
     * Returns {@code null} when the stack is not a registered Blighted item.</p>
     *
     * @param itemStack the item stack to resolve
     * @return the registered Blighted item, or {@code null} if none exists
     */
    public static BlightedItem fromItemStack(@NonNull ItemStack itemStack) {
        if (itemStack.getType().isAir()) return null;

        var meta = itemStack.getItemMeta();
        if (meta == null) return null;

        var container = meta.getPersistentDataContainer();
        String itemId = container.get(BLIGHTED_ID_KEY, PersistentDataType.STRING);
        if (itemId == null) return null;

        return ItemRegistry.getItem(itemId);
    }

    /**
     * Triggers all abilities matching the provided event.
     *
     * @param blightedPlayer the player owning the item
     * @param event          the event that may trigger abilities
     */
    public void triggerAbilities(BlightedPlayer blightedPlayer, Event event) {
        for (Ability ability : abilities) {
            if (ability.type().matches(event)) {
                AbilityExecutor.execute(ability, blightedPlayer, event);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.canPlace(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (!rule.canInteract(event, itemStack)) return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUse(Event event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.canUse(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Builds the item stack and applies BlightedMC persistent metadata.
     *
     * @return the configured item stack
     */
    @Override
    public ItemStack toItemStack() {
        setPersistentData(BLIGHTED_ID_KEY, PersistentDataType.STRING, itemId);
        setPersistentData(BLIGHTED_RARITY_KEY, PersistentDataType.STRING, itemRarity.name());
        return super.toItemStack();
    }

    /**
     * Creates an item stack instance.
     *
     * <p>This method delegates to {@link #toItemStack()}.</p>
     *
     * @return a newly built item stack
     */
    @Override
    public ItemStack get() {
        return this.toItemStack();
    }
}
