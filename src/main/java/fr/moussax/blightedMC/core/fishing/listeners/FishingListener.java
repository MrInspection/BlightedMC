package fr.moussax.blightedMC.core.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.fishing.RodType;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.fishing.LootTable.pools.*;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.FishHook;

import java.util.Objects;

public class FishingListener implements Listener {

  @EventHandler
  public void onPlayerFishing(PlayerFishEvent event) {
    Player player = event.getPlayer();
    BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
    FishHook hook = event.getHook();

    if (event.getState() == PlayerFishEvent.State.FISHING) {
      RodType rodType = ItemsRegistry.BLIGHTED_ITEMS.get(
          Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta())
            .getPersistentDataContainer().get(new NamespacedKey(BlightedMC.getInstance(), "id"), PersistentDataType.STRING))
        .getRodType();

      if (rodType == RodType.LAVA_FISHING_ROD) {
        int ticksUntilCatch = 60 + (int)(Math.random() * 40);
        new LavaFishingHook(hook, blightedPlayer, ticksUntilCatch);
        event.setCancelled(true);
      }
    }

    if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
      Material hookBlock = hook.getLocation().getBlock().getType();
      RodType rodType = ItemsRegistry.BLIGHTED_ITEMS.get(
          Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta())
            .getPersistentDataContainer().get(new NamespacedKey(BlightedMC.getInstance(), "id"), PersistentDataType.STRING))
        .getRodType();

      if (hookBlock == Material.LAVA && rodType == RodType.LAVA_FISHING_ROD) {
        event.setCancelled(true);
        LavaFishingHook.getHook(hook).reelIn();
        return;
      }

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
