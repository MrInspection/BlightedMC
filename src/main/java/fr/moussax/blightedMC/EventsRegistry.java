package fr.moussax.blightedMC;

import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.core.entities.listeners.SpawnableEntitiesListener;
import fr.moussax.blightedMC.core.entities.magic.LaserBeam;
import fr.moussax.blightedMC.core.items.ItemsRegistrySearch;
import fr.moussax.blightedMC.core.items.abilities.AbilityListener;
import fr.moussax.blightedMC.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.core.items.crafting.menu.CraftingTableListener;
import fr.moussax.blightedMC.core.items.rules.ItemRuleListener;
import fr.moussax.blightedMC.core.menus.MenuListener;
import fr.moussax.blightedMC.core.players.BlightedPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * Registers all event listeners required by the plugin.
 * Should be initialized once during plugin startup.
 */
public final class EventsRegistry {
  private final BlightedMC instance = BlightedMC.getInstance();
  private BlightedBlock.BlightedBlockListener blockListener;

  /**
   * Registers all listeners to the Bukkit PluginManager.
   */
  public void initializeListeners() {
    PluginManager pm = Bukkit.getPluginManager();
    pm.registerEvents(new MenuListener(), instance);
    pm.registerEvents(new BlightedEntitiesListener(), instance);
    pm.registerEvents(new SpawnableEntitiesListener(), instance);
    pm.registerEvents(new LaserBeam.LaserBeamListener(), instance);

    // Store reference to block listener
    blockListener = new BlightedBlock.BlightedBlockListener();
    pm.registerEvents(blockListener, instance);

    pm.registerEvents(new BlightedPlayerListener(), instance);
    pm.registerEvents(new CraftingTableListener(), instance);
    pm.registerEvents(new ItemRuleListener(), instance);
    pm.registerEvents(new AbilityListener(), instance);
    pm.registerEvents(new ItemsRegistrySearch(), instance);
  }

  /** Expose for saving data on shutdown */
  public BlightedBlock.BlightedBlockListener getBlockListener() {
    return blockListener;
  }
}
