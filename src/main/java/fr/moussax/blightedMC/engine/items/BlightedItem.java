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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
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
     * Adds multiple abilities to this item and injects their lore representations.
     *
     * @param abilities abilities to add
     */
    public void addAbilities(Ability... abilities) {
        for (Ability ability : abilities) {
            addAbility(ability, true);
        }
    }

    /**
     * Sets the full set bonus of this item.
     *
     * <p>The bonus description is automatically added to the item lore by default.</p>
     *
     * @param fullSetBonus the set bonus, or {@code null} to remove it
     */
    public void setFullSetBonus(FullSetBonus fullSetBonus) {
        setFullSetBonus(fullSetBonus, true);
    }

    /**
     * Sets the full set bonus of this item with optional lore injection.
     *
     * @param fullSetBonus the set bonus, or {@code null} to remove it
     * @param injectLore   whether the bonus description should be added to the item lore
     */
    public void setFullSetBonus(FullSetBonus fullSetBonus, boolean injectLore) {
        this.fullSetBonus = fullSetBonus;
        if (fullSetBonus == null) return;

        if (injectLore) {
            List<String> formattedLore = fullSetBonus.getBonusLore();
            this.addLore(formattedLore.toArray(new String[0]));
        }
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

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return null;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
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

    @Getter
    private ItemConsumeHandler consumeHandler;

    /**
     * Registers an explicit consume handler callback invoked with the consuming player and item stack.
     *
     * @param consumeHandler explicit item consume handler callback
     * @return this item manager for chaining
     */
    public BlightedItem onConsume(ItemConsumeHandler consumeHandler) {
        this.consumeHandler = consumeHandler;
        return this;
    }

    /**
     * Registers a simple consume handler callback invoked directly with the consuming player.
     *
     * @param consumeHandler consumer callback receiving the player
     * @return this item manager for chaining
     */
    public BlightedItem onConsume(Consumer<Player> consumeHandler) {
        this.consumeHandler = (player, _) -> consumeHandler.accept(player);
        return this;
    }

    /**
     * Checks whether this custom item is classified as equippable gear (weapon, armor, or tool).
     *
     * @return {@code true} if equipment, {@code false} otherwise
     */
    public boolean isEquipment() {
        if (itemType == null || itemType.getCategory() == null) return false;
        return switch (itemType.getCategory()) {
            case ARMOR, MELEE_WEAPON, RANGE_WEAPON, TOOLS -> true;
            default -> false;
        };
    }

    /**
     * Evaluates whether any registered rule restricts block placement for this item.
     *
     * @param event     block place event
     * @param itemStack item stack being placed
     * @return {@code true} if placement should be restricted, {@code false} otherwise
     */
    @Override
    public boolean shouldRestrictPlace(BlockPlaceEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictPlace(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Evaluates whether any registered rule restricts player interaction for this item.
     *
     * @param event     player interact event
     * @param itemStack item stack being interacted with
     * @return {@code true} if interaction should be restricted, {@code false} otherwise
     */
    @Override
    public boolean shouldRestrictInteract(PlayerInteractEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictInteract(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Evaluates whether any registered rule restricts generic event usage for this item.
     *
     * @param event     triggering event
     * @param itemStack item stack being used
     * @return {@code true} if usage should be restricted, {@code false} otherwise
     */
    @Override
    public boolean shouldRestrictUse(Event event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictUse(event, itemStack)) return true;
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
