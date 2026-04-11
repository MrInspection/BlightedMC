package fr.moussax.blightedMC.engine.items;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.items.abilities.Ability;
import fr.moussax.blightedMC.engine.items.abilities.AbilityExecutor;
import fr.moussax.blightedMC.engine.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import fr.moussax.blightedMC.engine.items.rules.ItemRule;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class BlightedItem extends ItemBuilder implements ItemRule, ItemFactory {
    public static final NamespacedKey BLIGHTED_ID_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_id");
    public static final NamespacedKey BLIGHTED_RARITY_KEY = new NamespacedKey(BlightedMC.getInstance(), "blighted_rarity");

    @Getter
    private final String itemId;
    @Getter
    private final ItemRarity itemRarity;
    @Getter
    private final ItemType itemType;
    @Setter
    @Getter
    private FullSetBonus fullSetBonus;
    @Getter
    private final List<Ability> abilities = new ArrayList<>();
    private final List<ItemRule> rules = new ArrayList<>();

    public BlightedItem(@NonNull String itemId, @NonNull ItemType type, @NonNull ItemRarity rarity, @NonNull Material material) {
        super(material);
        this.itemId = itemId;
        this.itemType = type;
        this.itemRarity = rarity;
    }

    public BlightedItem(@NonNull String itemId, @NonNull ItemType type, @NonNull ItemRarity rarity, @NonNull ItemStack itemStack) {
        super(itemStack);
        this.itemId = itemId;
        this.itemType = type;
        this.itemRarity = rarity;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public void addRule(ItemRule rule) {
        rules.add(rule);
    }

    @Override
    public BlightedItem setDisplayName(@NonNull String displayName) {
        super.setDisplayName(itemRarity.getColorPrefix() + displayName);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public BlightedItem isUnstackable() {
        super.setUnstackable(true);
        return this;
    }

    public static BlightedItem fromItemStack(@NonNull ItemStack itemStack) {
        if (itemStack.getType().isAir()) return null;

        var meta = itemStack.getItemMeta();
        if (meta == null) return null;

        var container = meta.getPersistentDataContainer();
        String itemId = container.get(BLIGHTED_ID_KEY, PersistentDataType.STRING);
        if (itemId == null) return null;

        return ItemRegistry.getItem(itemId);
    }

    public void triggerAbilities(BlightedPlayer blightedPlayer, Event event) {
        for (Ability ability : abilities) {
            if (ability.type().matches(event)) {
                AbilityExecutor.execute(ability, blightedPlayer, event);
            }
        }
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
}
