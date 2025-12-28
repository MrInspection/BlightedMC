package fr.moussax.blightedMC.smp.core.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.smp.core.fishing.FishingMethod;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class FishingListener implements Listener {
    private static final double CUSTOM_LOOT_CHANCE = BlightedMC.getInstance().getSettings().getCustomLootChance();

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            handleFishingCast(player, hook, blightedPlayer);
            return;
        }

        LavaFishingHook customHook = LavaFishingHook.get(hook);
        if (customHook != null) {
            handleLavaFishingReel(event, customHook);
            return;
        }

        handleStandardFishing(event, player, hook, blightedPlayer);
    }

    private void handleFishingCast(Player player, FishHook hook, BlightedPlayer blightedPlayer) {
        BlightedItem rod = getLavaRodFromInventory(player);
        if (rod == null) return;

        Material hookBlockType = hook.getLocation().getBlock().getType();
        if (hookBlockType == Material.WATER) {
            Formatter.warn(player, "Lava fishing rods cannot be used in water!");
            hook.remove();
            return;
        }

        new LavaFishingHook(hook, blightedPlayer);
    }

    private BlightedItem getLavaRodFromInventory(Player player) {
        BlightedItem mainHand = BlightedItem.fromItemStack(player.getInventory().getItemInMainHand());
        if (mainHand != null && mainHand.getItemType() == ItemType.LAVA_FISHING_ROD) {
            return mainHand;
        }

        BlightedItem offHand = BlightedItem.fromItemStack(player.getInventory().getItemInOffHand());
        if (offHand != null && offHand.getItemType() == ItemType.LAVA_FISHING_ROD) {
            return offHand;
        }

        return null;
    }

    private void handleLavaFishingReel(PlayerFishEvent event, LavaFishingHook customHook) {
        PlayerFishEvent.State state = event.getState();
        if (state == PlayerFishEvent.State.REEL_IN ||
            state == PlayerFishEvent.State.IN_GROUND ||
            state == PlayerFishEvent.State.CAUGHT_FISH) {
            customHook.reelIn();
        } else {
            customHook.remove();
        }
    }

    private void handleStandardFishing(PlayerFishEvent event, Player player, FishHook hook, BlightedPlayer blightedPlayer) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Entity caught = event.getCaught();
        if (!(caught instanceof Item caughtItem)) return;

        if (ThreadLocalRandom.current().nextDouble() > CUSTOM_LOOT_CHANCE) return;

        World.Environment environment = player.getWorld().getEnvironment();
        FishingLootTable lootTable = FishingLootRegistry.getTable(environment, FishingMethod.WATER);

        LivingEntity entity = lootTable.rollEntity(blightedPlayer, hook.getLocation(), caughtItem.getVelocity());
        if (entity != null) {
            caughtItem.remove();
            return;
        }

        ItemStack customItem = lootTable.rollItem(blightedPlayer);
        if (customItem != null) {
            caughtItem.setItemStack(customItem);
        }
    }
}
