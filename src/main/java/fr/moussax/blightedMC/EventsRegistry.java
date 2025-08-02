package fr.moussax.blightedMC;

import fr.moussax.blightedMC.core.entities.listeners.BlightedEntitiesListener;
import fr.moussax.blightedMC.core.entities.magic.LaserBeam;
import fr.moussax.blightedMC.core.items.abilities.AbilityListener;
import fr.moussax.blightedMC.core.items.blocks.BlightedBlock;
import fr.moussax.blightedMC.core.items.crafting.menu.CraftingTableListener;
import fr.moussax.blightedMC.core.items.rules.ItemRuleListener;
import fr.moussax.blightedMC.core.menus.MenuListeners;
import fr.moussax.blightedMC.core.players.BlightedPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public final class EventsRegistry {
  private final BlightedMC instance = BlightedMC.getInstance();

  public void initializeListeners() {
    PluginManager pm = Bukkit.getPluginManager();
    pm.registerEvents(new MenuListeners(), instance);
    pm.registerEvents(new BlightedEntitiesListener(), instance);
    pm.registerEvents(new LaserBeam.LaserBeamListener(), instance);
    pm.registerEvents(new BlightedBlock.BlightedBlockListener(), instance);
    pm.registerEvents(new BlightedPlayerListener(), instance);
    pm.registerEvents(new CraftingTableListener(), instance);
    pm.registerEvents(new ItemRuleListener(), instance);
    pm.registerEvents(new AbilityListener(), instance);
  }
}
