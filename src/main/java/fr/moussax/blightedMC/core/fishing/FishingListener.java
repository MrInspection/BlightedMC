package fr.moussax.blightedMC.core.fishing;

import fr.moussax.blightedMC.core.fishing.LootTable.pools.*;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.FishHook;

public class FishingListener implements Listener {

  @EventHandler
  public void onPlayerFishing(PlayerFishEvent event) {
    Player player = event.getPlayer();
    BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
    FishHook hook = event.getHook();

    if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
      Material hookBlock = hook.getLocation().getBlock().getType();

      // Water fishing: give custom loot but keep vanilla
      if (hookBlock == Material.WATER) {
        World.Environment env = player.getWorld().getEnvironment();
        if (env == World.Environment.THE_END) {
          EndFishingPool pool = new EndFishingPool();
          if (pool.getItemDrop(blightedPlayer) != null)
            player.getInventory().addItem(pool.getItemDrop(blightedPlayer));
        } else {
          OverworldFishingPool pool = new OverworldFishingPool();
          if (pool.getItemDrop(blightedPlayer) != null)
            player.getInventory().addItem(pool.getItemDrop(blightedPlayer));
        }
      }
    }
  }
}
