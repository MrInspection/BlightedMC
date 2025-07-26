package fr.moussax.blightedMC;

import fr.moussax.blightedMC.managers.EventsManager;
import fr.moussax.blightedMC.utils.CommandBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlightedMC extends JavaPlugin {
  private static BlightedMC instance;

  @Override
  public void onEnable() {
    instance = this;
    CommandBuilder.initialize(this);
    CommandBuilder.initializeCommands();
    new EventsManager().registerListeners();
  }

  public static BlightedMC getInstance() {
    return instance;
  }
}
