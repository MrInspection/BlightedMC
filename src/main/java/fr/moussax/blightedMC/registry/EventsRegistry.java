package fr.moussax.blightedMC.registry;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.engine.entities.listeners.EntityComponentListener;
import fr.moussax.blightedMC.engine.entities.listeners.SpawnableEntitiesListener;
import fr.moussax.blightedMC.engine.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.engine.fishing.FishingListener;
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
import fr.moussax.blightedMC.engine.quest.BlightedQuestListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * Registers and manages the event listeners used by BlightedMC.
 *
 * <p>This registry is responsible for constructing listener-dependent
 * subsystems, registering Bukkit event listeners, and managing the lifecycle
 * of listeners that require explicit initialization or cleanup.</p>
 */
public final class EventsRegistry {

    private final BlightedMC instance = BlightedMC.getInstance();
    @Getter
    private MenuSystem menuSystem;
    @Getter
    private MenuManager menuManager;
    private SpawnableEntitiesListener spawnableEntitiesListener;
    private SignInputListener signInputListener;

    /**
     * Initializes the event-driven subsystems and registers all BlightedMC
     * event listeners with Bukkit.
     *
     * <p>The spawnable entity listener is also registered as a callback on
     * {@link EntitiesRegistry} so that its cache can be invalidated whenever
     * the entity registry changes.</p>
     */
    public void initializeListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        menuSystem = new MenuSystem(instance);
        menuManager = new MenuManager(menuSystem);
        spawnableEntitiesListener = new SpawnableEntitiesListener();
        EntitiesRegistry.addOnRegisterCallback(spawnableEntitiesListener::invalidateCache);
        signInputListener = new SignInputListener();

        pm.registerEvents(new MenuListener(menuSystem), instance);
        pm.registerEvents(signInputListener, instance);
        pm.registerEvents(new BlightedEntitiesListener(), instance);
        pm.registerEvents(new EntityComponentListener(), instance);
        pm.registerEvents(spawnableEntitiesListener, instance);
        pm.registerEvents(new BlightedBlockListener(), instance);
        pm.registerEvents(new PlayerListener(), instance);
        pm.registerEvents(new ItemRuleListener(), instance);
        pm.registerEvents(new AbilityListener(), instance);
        pm.registerEvents(new FishingListener(), instance);
        pm.registerEvents(new UnsafeAnvilListener(), instance);
        pm.registerEvents(new WitherImpactAbility(), instance);
        pm.registerEvents(new BlightedQuestListener(), instance);
    }

    /**
     * Builds the spawnable entity cache after the entity registry has been initialized.
     *
     * <p>If the spawnable entity listener has not been initialized, this method has no effect.</p>
     */
    public void buildSpawnCache() {
        if (spawnableEntitiesListener != null) {
            spawnableEntitiesListener.rebuildCache();
        }
    }

    /**
     * Cleans up listener-specific resources that require explicit disposal.
     *
     * <p>If the sign input listener has not been initialized, this method has no effect.</p>
     */
    public void cleanup() {
        if (signInputListener != null) {
            signInputListener.cleanup();
        }
    }

    /**
     * Shuts down the menu system and releases its associated resources.
     *
     * <p>If the menu system has not been initialized, this method has no effect.</p>
     */
    public void shutdownMenus() {
        if (menuSystem != null) {
            menuSystem.shutdown();
        }
    }
}
