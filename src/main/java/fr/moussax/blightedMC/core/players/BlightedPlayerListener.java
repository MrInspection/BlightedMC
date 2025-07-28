package fr.moussax.blightedMC.core.players;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BlightedPlayerListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    new BlightedPlayer(event.getPlayer());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    BlightedPlayer player = BlightedPlayer.getBlightedPlayer(event.getPlayer());
    if(player != null) {
      player.saveData();
      BlightedPlayer.remove(event.getPlayer());
    }
  }
}
