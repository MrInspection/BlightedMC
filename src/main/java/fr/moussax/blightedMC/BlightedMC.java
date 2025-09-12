package fr.moussax.blightedMC;

import fr.moussax.blightedMC.commands.CommandBuilder;
import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.registry.RegistrySystem;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlightedMC extends JavaPlugin {
  private static BlightedMC instance;
  private EventsRegistry eventsRegistry;

  @Override
  public void onEnable() {
    instance = this;

    RegistrySystem.initializeAllRegistries();
    eventsRegistry = new EventsRegistry();
    eventsRegistry.initializeListeners();

    CommandBuilder.initialize(this);
    CommandBuilder.initializeCommands();

    // Rehydrate any loaded entities in already-loaded chunks (e.g., after /reload)
    // by manually firing the chunk scan once on enabling.
    // This complements the runtime ChunkLoadEvent handler.
    getServer().getWorlds().forEach(world -> {
      for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
        BlightedEntitiesListener.rehydrateChunk(chunk);
      }
    });
  }

  @Override
  public void onDisable() {
    // Persist block data on shutdown
    if (eventsRegistry != null && eventsRegistry.getBlockListener() != null) {
      eventsRegistry.getBlockListener().saveData();
    }
    RegistrySystem.clearAllRegistries();
  }

  public static BlightedMC getInstance() {
    return instance;
  }
}
