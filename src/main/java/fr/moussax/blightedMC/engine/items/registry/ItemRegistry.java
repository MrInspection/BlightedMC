package fr.moussax.blightedMC.engine.items.registry;

import fr.moussax.blightedMC.content.items.*;
import fr.moussax.blightedMC.content.items.armors.FishingArmors;
import fr.moussax.blightedMC.content.items.armors.HomodeusArmor;
import fr.moussax.blightedMC.content.items.armors.RocketBoots;
import fr.moussax.blightedMC.content.items.blocks.BlightedBlockItems;
import fr.moussax.blightedMC.content.items.materials.BlightedMaterials;
import fr.moussax.blightedMC.content.items.materials.EndMaterials;
import fr.moussax.blightedMC.content.items.materials.NetherMaterials;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class ItemRegistry {
    private static final Map<String, BlightedItem> REGISTERED_ITEMS = new HashMap<>();

    private static final List<ItemProvider> MODULES = List.of(
        new BlightedGemstone(),
        new BlightedMaterials(),
        new Bonemerang(),
        new GlimmeringEye(),
        new KnightsSword(),
        new HomodeusArmor(),
        new RocketBoots(),
        new BlightedBlockItems(),
        new Hyperion(),
        new ThermalFuels(),
        new BlightedTools(),
        new NetherMaterials(),
        new EndMaterials(),
        new FishingArmors()
    );

    private ItemRegistry() {
    }

    public static void initialize() {
        clear();
        MODULES.forEach(ItemProvider::register);
        Log.success("ItemDirectory", "Registered " + REGISTERED_ITEMS.size() + " custom items.");
    }

    @Nullable
    public static BlightedItem fromItemStack(@NonNull ItemStack itemStack) {
        return BlightedItem.fromItemStack(itemStack);
    }

    static void addItem(@NonNull BlightedItem blightedItem) {
        if (REGISTERED_ITEMS.containsKey(blightedItem.getItemId())) {
            throw new IllegalArgumentException("Duplicate item ID: " + blightedItem.getItemId());
        }
        REGISTERED_ITEMS.put(blightedItem.getItemId(), blightedItem);
    }

    @NonNull
    public static BlightedItem getItem(@NonNull String itemId) {
        BlightedItem item = REGISTERED_ITEMS.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Unknown item ID: " + itemId);
        }
        return item;
    }

    public static List<BlightedItem> getAllItems() {
        return List.copyOf(REGISTERED_ITEMS.values());
    }

    public static void clear() {
        REGISTERED_ITEMS.clear();
    }
}
