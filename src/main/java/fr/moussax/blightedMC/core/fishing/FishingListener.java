package fr.moussax.blightedMC.core.fishing;

import fr.moussax.blightedMC.core.fishing.environment.EndFishing;
import fr.moussax.blightedMC.core.fishing.environment.OverworldFishing;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FishingListener implements Listener {
  private static final Random RANDOM = new Random();
  private static final double CUSTOM_LOOT_CHANCE = 0.50; // 50% chance for custom loot, 50% for vanilla

  @EventHandler
  public void onPlayerFishing(PlayerFishEvent event) {
    if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
      return;
    }

    Player player = event.getPlayer();
    Entity caught = event.getCaught();

    if (!(caught instanceof Item caughtItem)) {
      return;
    }

    if (RANDOM.nextDouble() > CUSTOM_LOOT_CHANCE) {
      return; // Keep vanilla loot
    }

    BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
    FishingLootTable lootTable = getLootTableForEnvironment(player.getWorld().getEnvironment());
    FishHook hook = event.getHook();
    Location hookLocation = hook.getLocation();

    LivingEntity entity = lootTable.rollEntity(blightedPlayer, hookLocation, caughtItem.getVelocity());

    if (entity != null) {
      caughtItem.remove();
      return;
    }

    ItemStack customItem = lootTable.rollItem(blightedPlayer);

    if (customItem != null) {
      caughtItem.setItemStack(customItem);
    }
  }

  private FishingLootTable getLootTableForEnvironment(World.Environment environment) {
    if(environment == World.Environment.THE_END) {
      return EndFishing.create();
    } else {
      return OverworldFishing.create();
    }
  }
}
