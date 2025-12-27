package fr.moussax.blightedMC.smp.core.items;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.abilities.Ability;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityExecutor;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityType;
import fr.moussax.blightedMC.smp.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customizable item template in BlightedMC.
 * <p>
 * This class extends {@link ItemBuilder} to define items with specific identifiers, rarity,
 * type, abilities, interaction rules, and optional full set bonuses.
 * It handles persistent metadata storage and ability execution logic.
 *
 * @see fr.moussax.blightedMC.smp.core.items.abilities.Ability
 * @see fr.moussax.blightedMC.smp.core.items.abilities.FullSetBonus
 * @see fr.moussax.blightedMC.smp.core.items.rules.ItemRule
 */
public class BlightedItem extends ItemBuilder implements ItemRule, ItemFactory {
    public static final NamespacedKey BLIGHTED_ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_id");
    public static final NamespacedKey BLIGHTED_RARITY_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_rarity");

    private final String itemId;
    private final ItemRarity itemRarity;
    private final ItemType itemType;
    private FullSetBonus fullSetBonus;

    private final List<Ability> abilities = new ArrayList<>();
    private final List<ItemRule> rules = new ArrayList<>();

    /**
     * Creates a new item template with the given parameters.
     *
     * @param itemId   the unique item identifier
     * @param type     the item type
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
     * Creates a new item template from an existing {@link ItemStack}.
     *
     * @param itemId    the unique item identifier
     * @param type      the item type
     * @param rarity    the item rarity
     * @param itemStack the source item stack
     */
    public BlightedItem(@NonNull String itemId, @NonNull ItemType type, @NonNull ItemRarity rarity, @NonNull ItemStack itemStack) {
        super(itemStack);
        this.itemId = itemId;
        this.itemType = type;
        this.itemRarity = rarity;
    }

    /**
     * Adds an ability to this item.
     *
     * @param ability the ability to add
     */
    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    /**
     * Adds a rule governing how the item can be used or placed.
     *
     * @param rule the rule to add
     */
    public void addRule(ItemRule rule) {
        rules.add(rule);
    }

    /**
     * Returns the list of abilities associated with this item.
     *
     * @return the list of abilities
     */
    public List<Ability> getAbilities() {
        return abilities;
    }

    @Override
    public BlightedItem setDisplayName(@NonNull String displayName) {
        super.setDisplayName(itemRarity.getColorPrefix() + displayName);
        return this;
    }

    /**
     * Marks this item as unstackable.
     *
     * @return this instance for chaining
     */
    public BlightedItem isUnstackable() {
        super.setUnstackable(true);
        return this;
    }

    /**
     * Assigns a full set bonus to this item.
     *
     * @param bonus the full set bonus to assign
     */
    public void setFullSetBonus(FullSetBonus bonus) {
        this.fullSetBonus = bonus;
    }

    /**
     * Returns the full set bonus of this item, if any.
     *
     * @return the full set bonus or {@code null}
     */
    public FullSetBonus getFullSetBonus() {
        return fullSetBonus;
    }

    /**
     * Builds an {@link BlightedItem} from an existing {@link ItemStack}.
     *
     * @param itemStack the item stack
     * @return the corresponding item template or {@code null} if not registered
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
     * Triggers all abilities of this item that match the provided event.
     *
     * @param blightedPlayer the player using the item
     * @param event          the event triggering abilities
     */
    public void triggerAbilities(BlightedPlayer blightedPlayer, Event event) {
        for (Ability ability : abilities) {
            if (shouldTriggerAbility(ability, event)) {
                AbilityExecutor.execute(ability, blightedPlayer, event);
            }
        }

        for (FullSetBonus activeBonus : blightedPlayer.getActiveFullSetBonuses()) {
            if (activeBonus.getPieces() >= activeBonus.getMaxPieces()) {
                activeBonus.activate();
            }
        }
    }

    private boolean shouldTriggerAbility(Ability ability, Event event) {
        if (event instanceof PlayerInteractEvent interactEvent) {
            Action action = interactEvent.getAction();
            AbilityType abilityType = ability.type();

            if (abilityType.isSneak() && !interactEvent.getPlayer().isSneaking()) {
                return false;
            }

            boolean rightClick = abilityType.isRightClick() &&
                (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);

            boolean leftClick = abilityType.isLeftClick() &&
                (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK);

            return rightClick || leftClick;
        }
        return true;
    }

    @Override
    public boolean canPlace(BlockPlaceEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.canPlace(event, itemStack)) return true;
        }
        return false;
    }

    @Override
    public boolean canInteract(PlayerInteractEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (!rule.canInteract(event, itemStack)) return false;
        }
        return true;
    }

    @Override
    public boolean canUse(Event event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.canUse(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Converts this item template to a Bukkit {@link ItemStack} with persistent metadata.
     *
     * @return the generated item stack
     */
    @Override
    public ItemStack toItemStack() {
        this.setItemMeta(itemMeta -> {
            var container = itemMeta.getPersistentDataContainer();
            container.set(BLIGHTED_ID_KEY, PersistentDataType.STRING, itemId);
            container.set(BLIGHTED_RARITY_KEY, PersistentDataType.STRING, itemRarity.name());
        });
        return super.toItemStack();
    }

    @Override
    public ItemStack createItemStack() {
        return this.toItemStack();
    }

    public String getItemId() {
        return itemId;
    }

    public ItemRarity getItemRarity() {
        return itemRarity;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
