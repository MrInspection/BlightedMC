package fr.moussax.blightedMC;

import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.core.entities.listeners.SpawnableEntitiesListener;
import fr.moussax.blightedMC.core.fishing.FishingListener;
import fr.moussax.blightedMC.core.items.abilities.AbilityListener;
import fr.moussax.blightedMC.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.core.items.crafting.menu.CraftingTableListener;
import fr.moussax.blightedMC.core.items.registry.ItemDirectorySearch;
import fr.moussax.blightedMC.core.items.rules.ItemRuleListener;
import fr.moussax.blightedMC.core.menus.MenuListener;
import fr.moussax.blightedMC.core.player.BlightedPlayerListener;
import fr.moussax.blightedMC.game.abilities.WitherImpactAbility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public final class EventsRegistry {
    private final BlightedMC instance = BlightedMC.getInstance();
    private BlightedBlock.BlightedBlockListener blockListener;

    public void initializeListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new MenuListener(), instance);
        pm.registerEvents(new BlightedEntitiesListener(), instance);
        pm.registerEvents(new SpawnableEntitiesListener(), instance);

        blockListener = new BlightedBlock.BlightedBlockListener();
        pm.registerEvents(blockListener, instance);
        pm.registerEvents(new BlightedPlayerListener(), instance);
        pm.registerEvents(new CraftingTableListener(), instance);
        pm.registerEvents(new ItemRuleListener(), instance);
        pm.registerEvents(new AbilityListener(), instance);
        pm.registerEvents(new ItemDirectorySearch(), instance);
        pm.registerEvents(new FishingListener(), instance);
        pm.registerEvents(new WitherImpactAbility(), instance);
    }

    public BlightedBlock.BlightedBlockListener getBlockListener() {
        return blockListener;
    }
}
