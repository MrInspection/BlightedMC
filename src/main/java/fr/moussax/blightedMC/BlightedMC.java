package fr.moussax.blightedMC;

import fr.moussax.blightedMC.commands.CommandBuilder;
import fr.moussax.blightedMC.core.registry.RegistrySystem;
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
