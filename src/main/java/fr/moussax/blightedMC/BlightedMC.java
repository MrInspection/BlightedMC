package fr.moussax.blightedMC;

import fr.moussax.blightedMC.core.registry.RegistrySystem;
import fr.moussax.blightedMC.commands.CommandBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlightedMC extends JavaPlugin {
  private static BlightedMC instance;

  @Override
  public void onEnable() {
    instance = this;
    RegistrySystem.initializeAllRegistries();
    
    new EventsRegistry().initializeListeners();
    CommandBuilder.initialize(this);
    CommandBuilder.initializeCommands();
  }

  @Override
  public void onDisable() {
    RegistrySystem.clearAllRegistries();
  }

  public static BlightedMC getInstance() {
    return instance;
  }
}
