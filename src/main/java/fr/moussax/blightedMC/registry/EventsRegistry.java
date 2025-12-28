package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.smp.core.entities.listeners.SpawnableEntitiesListener;
import fr.moussax.blightedMC.smp.core.fishing.listeners.FishingListener;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityListener;
import fr.moussax.blightedMC.smp.core.items.blocks.BlightedBlockListener;
import fr.moussax.blightedMC.smp.core.items.crafting.listener.CraftingTableListener;
import fr.moussax.blightedMC.smp.core.items.listeners.UnsafeAnvilListener;
import fr.moussax.blightedMC.smp.core.items.registry.menu.ItemRegistrySearch;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRuleListener;
import fr.moussax.blightedMC.smp.core.menus.MenuListener;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayerListener;
import fr.moussax.blightedMC.smp.features.abilities.WitherImpactAbility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public final class EventsRegistry {
    private final BlightedMC instance = BlightedMC.getInstance();
    private static BlightedBlockListener blockListener;

    public void initializeListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new MenuListener(), instance);
        pm.registerEvents(new BlightedEntitiesListener(), instance);
        pm.registerEvents(new SpawnableEntitiesListener(), instance);

        blockListener = new BlightedBlockListener();
        pm.registerEvents(blockListener, instance);
        pm.registerEvents(new BlightedPlayerListener(), instance);
        pm.registerEvents(new CraftingTableListener(), instance);
        pm.registerEvents(new ItemRuleListener(), instance);
        pm.registerEvents(new AbilityListener(), instance);
        pm.registerEvents(new ItemRegistrySearch(), instance);
        pm.registerEvents(new FishingListener(), instance);
        pm.registerEvents(new UnsafeAnvilListener(), instance);
        pm.registerEvents(new WitherImpactAbility(), instance);
    }

    public BlightedBlockListener getBlockListener() {
        return blockListener;
    }
}
