package fr.moussax.blightedMC.managers;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class EventsManager {
  private final BlightedMC instance = BlightedMC.getInstance();

  public void registerListeners() {
    PluginManager pm = Bukkit.getPluginManager();
    pm.registerEvents(new BlightedEntitiesListener(), instance);
  }
}
