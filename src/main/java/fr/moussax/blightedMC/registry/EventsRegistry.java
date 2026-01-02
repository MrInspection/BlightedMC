package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.smp.core.entities.listeners.SpawnableEntitiesListener;
import fr.moussax.blightedMC.smp.core.fishing.listeners.FishingListener;
import fr.moussax.blightedMC.smp.core.items.abilities.AbilityListener;
import fr.moussax.blightedMC.smp.core.items.blocks.BlightedBlockListener;
import fr.moussax.blightedMC.smp.core.items.listeners.UnsafeAnvilListener;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRuleListener;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayerListener;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.system.MenuListener;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.system.MenuManager;
import fr.moussax.blightedMC.smp.core.shared.ui.menu.system.MenuSystem;
import fr.moussax.blightedMC.smp.core.shared.ui.sign.SignInputListener;
import fr.moussax.blightedMC.smp.features.items.abilities.WitherImpactAbility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public final class EventsRegistry {
    private final BlightedMC instance = BlightedMC.getInstance();
    private MenuSystem menuSystem;
    private MenuManager menuManager;
    private SpawnableEntitiesListener spawnableEntitiesListener;
    private SignInputListener signInputListener;

    public void initializeListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        menuSystem = new MenuSystem(instance);
        menuManager = new MenuManager(menuSystem);
        spawnableEntitiesListener = new SpawnableEntitiesListener();
        signInputListener = new SignInputListener();

        pm.registerEvents(new MenuListener(menuSystem), instance);
        pm.registerEvents(signInputListener, instance);
        pm.registerEvents(new BlightedEntitiesListener(), instance);
        pm.registerEvents(spawnableEntitiesListener, instance);
        pm.registerEvents(new BlightedBlockListener(), instance);
        pm.registerEvents(new BlightedPlayerListener(), instance);
        pm.registerEvents(new ItemRuleListener(), instance);
        pm.registerEvents(new AbilityListener(), instance);
        pm.registerEvents(new FishingListener(), instance);
        pm.registerEvents(new UnsafeAnvilListener(), instance);
        pm.registerEvents(new WitherImpactAbility(), instance);
    }

    public void buildSpawnCache() {
        if (spawnableEntitiesListener != null) {
            spawnableEntitiesListener.rebuildCache();
        }
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public MenuSystem getMenuSystem() {
        return menuSystem;
    }

    public void cleanup() {
        if (signInputListener != null) {
            signInputListener.cleanup();
        }
    }

    public void shutdownMenus() {
        if (menuSystem != null) {
            menuSystem.shutdown();
        }
    }
}
