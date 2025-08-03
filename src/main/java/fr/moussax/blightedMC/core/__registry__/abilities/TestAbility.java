package fr.moussax.blightedMC.core.__registry__.abilities;

import fr.moussax.blightedMC.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;

public class TestAbility implements AbilityManager<PlayerInteractEvent> {
  @Override
  public boolean triggerAbility(PlayerInteractEvent event) {
    Bukkit.getLogger().info("[DEBUG] TestAbility.triggerAbility() called for " + event.getPlayer().getName());
    return true;
  }

  @Override
  public int getCooldownSeconds() {
    return 2;
  }

  @Override
  public int getManaCost() {
    return 10;
  }

  @Override
  public boolean canTrigger(BlightedPlayer player) {
    Bukkit.getLogger().info("[DEBUG] TestAbility.canTrigger() called for " + player.getPlayer().getName());
    return true;
  }

  @Override
  public void start(BlightedPlayer player) {
    player.getPlayer().sendMessage("§8 ■ §7Test ability triggered!");
    Bukkit.getLogger().info("[DEBUG] Test ability executed for " + player.getPlayer().getName());
  }

  @Override
  public void stop(BlightedPlayer player) {

  }
}
