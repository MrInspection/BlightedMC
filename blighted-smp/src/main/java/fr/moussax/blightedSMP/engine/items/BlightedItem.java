package fr.moussax.blightedSMP.engine.items;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.items.abilities.Ability;
import fr.moussax.blightedSMP.engine.items.abilities.AbilityExecutor;
import fr.moussax.blightedSMP.engine.items.abilities.FullSetBonus;
import fr.moussax.blightedSMP.engine.items.recipes.RecipePreviewManager;
import fr.moussax.blightedSMP.engine.items.registry.ItemRegistry;
import fr.moussax.blightedSMP.engine.items.rules.ItemRule;
import fr.moussax.blightedSMP.engine.items.rules.ItemRuleEngine;
import fr.moussax.blightedSMP.engine.player.BlightedPlayer;
import fr.moussax.bedrock.ui.menu.Menu;
import fr.moussax.bedrock.utils.ItemBuilder;
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
 * Custom item definition with persistent identity, rarity, abilities, and restriction rules.
 *
 * <p>Extends {@link ItemBuilder} to provide fluent item construction while integrating with the
 * BlightedMC item system (ability execution, set bonuses, restriction rules, and recipe previews).
 * Built stacks are marked with persistent metadata keys {@link #BLIGHTED_ID_KEY} and
 * {@link #BLIGHTED_RARITY_KEY}.</p>
 */
public final class BlightedItem extends ItemBuilder implements ItemRule, Supplier<ItemStack> {

    public static final NamespacedKey BLIGHTED_ID_KEY = new NamespacedKey(BlightedSMP.getInstance(), "blighted_id");
    public static final NamespacedKey BLIGHTED_RARITY_KEY = new NamespacedKey(BlightedSMP.getInstance(), "blighted_rarity");

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
    @Getter
    private final ItemRuleEngine ruleEngine = new ItemRuleEngine();
    @Getter
    private boolean recipePreviewEnabled = false;

    /**
     * Constructs a custom item definition from a base material.
     *
     * @param itemId   unique item identifier
     * @param type     item category
     * @param rarity   item rarity
     * @param material base material
     */
    public BlightedItem(@NonNull String itemId, @NonNull ItemType type, @NonNull ItemRarity rarity, @NonNull Material material) {
        super(material);
        this.itemId = itemId;
        this.itemType = type;
        this.itemRarity = rarity;
    }

    /**
     * Constructs a custom item definition from an existing item stack.
     *
     * @param itemId    unique item identifier
     * @param type      item category
     * @param rarity    item rarity
     * @param itemStack base item stack
     */
    public BlightedItem(@NonNull String itemId, @NonNull ItemType type, @NonNull ItemRarity rarity, @NonNull ItemStack itemStack) {
        super(itemStack);
        this.itemId = itemId;
        this.itemType = type;
        this.itemRarity = rarity;
    }

    /**
     * Adds an ability to this item and appends its formatted description to the lore.
     *
     * @param ability ability to add
     */
    public void addAbility(Ability ability) {
        addAbility(ability, true);
    }

    /**
     * Adds an ability to this item with optional lore injection.
     *
     * @param ability    ability to add
     * @param injectLore {@code true} to append formatted ability description to lore
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
     * Adds multiple abilities to this item and appends their descriptions to the lore.
     *
     * @param abilities abilities to add
     */
    public void addAbilities(Ability... abilities) {
        for (Ability ability : abilities) {
            addAbility(ability, true);
        }
    }

    /**
     * Sets the full-set bonus for this item and appends its description to the lore.
     *
     * @param fullSetBonus set bonus to assign, or {@code null} to remove
     */
    public void setFullSetBonus(FullSetBonus fullSetBonus) {
        setFullSetBonus(fullSetBonus, true);
    }

    /**
     * Sets the full-set bonus for this item with optional lore injection.
     *
     * @param fullSetBonus set bonus to assign, or {@code null} to remove
     * @param injectLore   {@code true} to append set bonus description to lore
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
     * Registers a gameplay restriction rule for this item.
     *
     * @param rule restriction rule to add
     */
    public void addRule(ItemRule rule) {
        ruleEngine.addRule(rule);
    }

    /**
     * Enables recipe preview support for this item.
     */
    public void enableRecipePreview() {
        this.recipePreviewEnabled = true;
    }

    /**
     * Opens the recipe preview interface for a player.
     *
     * @param player     player viewing the preview
     * @param parentMenu parent menu to return to, or {@code null} for no parent
     * @return {@code true} if the preview opened successfully, {@code false} otherwise
     */
    public boolean openRecipePreview(@NonNull Player player, @Nullable Menu parentMenu) {
        return RecipePreviewManager.openPreview(player, this, parentMenu);
    }

    /**
     * Opens the recipe preview interface for a player without a parent menu.
     *
     * @param player player viewing the preview
     * @return {@code true} if the preview opened successfully, {@code false} otherwise
     */
    public boolean openRecipePreview(@NonNull Player player) {
        return openRecipePreview(player, null);
    }

    /**
     * Sets the item display name, prefixing it with the rarity color code.
     *
     * @param displayName new display name
     * @return this item builder instance
     */
    @Override
    public BlightedItem setDisplayName(@NonNull String displayName) {
        super.setDisplayName(itemRarity.getColorPrefix() + displayName);
        return this;
    }

    /**
     * Resolves the registered custom item definition corresponding to an item stack.
     *
     * @param itemStack item stack to inspect
     * @return registered custom item definition, or {@code null} if stack is non-custom or unregistered
     */
    @Nullable
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
     * Triggers active abilities on this item that match a triggering event.
     *
     * @param blightedPlayer player context executing the ability
     * @param event          triggering event
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
     * Registers an item consume handler receiving both the player and item stack.
     *
     * @param consumeHandler consume handler callback
     * @return this item instance for method chaining
     */
    public BlightedItem onConsume(ItemConsumeHandler consumeHandler) {
        this.consumeHandler = consumeHandler;
        return this;
    }

    /**
     * Registers a simple consume handler receiving the consuming player.
     *
     * @param consumeHandler consume callback receiving the player
     */
    public void onConsume(Consumer<Player> consumeHandler) {
        this.consumeHandler = (player, _) -> consumeHandler.accept(player);
    }

    /**
     * Evaluates whether this item is classified as equippable gear (weapon, armor, or tool).
     *
     * @return {@code true} if equippable equipment, {@code false} otherwise
     */
    public boolean isEquipment() {
        if (itemType == null || itemType.getCategory() == null) return false;
        return switch (itemType.getCategory()) {
            case ARMOR, MELEE_WEAPON, RANGE_WEAPON, TOOLS -> true;
            default -> false;
        };
    }

    /**
     * Evaluates whether block placement should be restricted for an item stack.
     *
     * @param event     block place event
     * @param itemStack item stack being placed
     * @return {@code true} if placement is restricted, {@code false} otherwise
     */
    @Override
    public boolean shouldRestrictPlace(BlockPlaceEvent event, ItemStack itemStack) {
        return ruleEngine.shouldRestrictPlace(event, itemStack);
    }

    /**
     * Evaluates whether player interaction should be restricted for an item stack.
     *
     * @param event     player interact event
     * @param itemStack item stack being interacted with
     * @return {@code true} if interaction is restricted, {@code false} otherwise
     */
    @Override
    public boolean shouldRestrictInteract(PlayerInteractEvent event, ItemStack itemStack) {
        return ruleEngine.shouldRestrictInteract(event, itemStack);
    }

    /**
     * Evaluates whether generic usage should be restricted for an event and item stack.
     *
     * @param event     triggering event
     * @param itemStack item stack being used
     * @return {@code true} if usage is restricted, {@code false} otherwise
     */
    @Override
    public boolean shouldRestrictUse(Event event, ItemStack itemStack) {
        return ruleEngine.shouldRestrictUse(event, itemStack);
    }

    /**
     * Builds the item stack and applies BlightedMC persistent metadata.
     *
     * @return configured item stack
     */
    @Override
    public ItemStack toItemStack() {
        setPersistentData(BLIGHTED_ID_KEY, PersistentDataType.STRING, itemId);
        setPersistentData(BLIGHTED_RARITY_KEY, PersistentDataType.STRING, itemRarity.name());
        return super.toItemStack();
    }

    /**
     * Creates an item stack instance by delegating to {@link #toItemStack()}.
     *
     * @return newly built item stack
     */
    @Override
    public ItemStack get() {
        return this.toItemStack();
    }
}
