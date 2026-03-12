package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.engine.entities.listeners.SpawnableEntitiesListener;
import fr.moussax.blightedMC.engine.fishing.listeners.FishingListener;
import fr.moussax.blightedMC.engine.items.abilities.AbilityListener;
import fr.moussax.blightedMC.engine.items.blocks.BlightedBlockListener;
import fr.moussax.blightedMC.engine.items.listeners.UnsafeAnvilListener;
import fr.moussax.blightedMC.engine.items.rules.ItemRuleListener;
import fr.moussax.blightedMC.engine.player.PlayerListener;
import fr.moussax.blightedMC.shared.ui.menu.system.MenuListener;
import fr.moussax.blightedMC.shared.ui.menu.system.MenuManager;
import fr.moussax.blightedMC.shared.ui.menu.system.MenuSystem;
import fr.moussax.blightedMC.shared.ui.sign.SignInputListener;
import fr.moussax.blightedMC.content.items.abilities.WitherImpactAbility;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public final class EventsRegistry {

    private final BlightedMC instance = BlightedMC.getInstance();
    @Getter
    private MenuSystem menuSystem;
    @Getter
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
        pm.registerEvents(new PlayerListener(), instance);
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
