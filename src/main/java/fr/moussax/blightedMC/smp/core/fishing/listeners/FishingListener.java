package fr.moussax.blightedMC.smp.core.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.FishingMethod;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.ThreadLocalRandom;

public class FishingListener implements Listener {
    private static final double CUSTOM_LOOT_CHANCE = BlightedMC.getInstance().getSettings().getCustomLootChance();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            handleFishingCast(player, hook, blightedPlayer, event);
            return;
        }

        LavaFishingHook customHook = LavaFishingHook.get(hook);
        if (customHook != null) {
            handleLavaFishingReel(event, customHook, player);
            return;
        }

        handleStandardFishing(event, player, hook, blightedPlayer);
    }

    private void handleFishingCast(Player player, FishHook hook, BlightedPlayer blightedPlayer, PlayerFishEvent event) {
        BlightedItem rod = getLavaRodFromInventory(player);
        if (rod == null) return;

        Material hookBlockType = hook.getLocation().getBlock().getType();
        if (hookBlockType == Material.WATER) {
            Formatter.warn(player, "Lava fishing rods cannot be used in water!");
            event.setCancelled(true);
            return;
        }

        new LavaFishingHook(hook, blightedPlayer, player);
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

    private void handleLavaFishingReel(PlayerFishEvent event, LavaFishingHook customHook, Player player) {
        PlayerFishEvent.State state = event.getState();
        if (state == PlayerFishEvent.State.REEL_IN ||
            state == PlayerFishEvent.State.IN_GROUND ||
            state == PlayerFishEvent.State.CAUGHT_FISH) {

            boolean success = customHook.reelIn();
            if (success) {
                damageRod(player);
            }
        } else {
            customHook.remove();
        }
    }

    private void damageRod(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack rodItemStack = null;

        BlightedItem mainBlighted = BlightedItem.fromItemStack(mainHand);
        if (mainBlighted != null && mainBlighted.getItemType() == ItemType.LAVA_FISHING_ROD) {
            rodItemStack = mainHand;
        } else {
            ItemStack offHand = player.getInventory().getItemInOffHand();
            BlightedItem offBlighted = BlightedItem.fromItemStack(offHand);
            if (offBlighted != null && offBlighted.getItemType() == ItemType.LAVA_FISHING_ROD) {
                rodItemStack = offHand;
            }
        }

        if (rodItemStack == null || rodItemStack.getType().getMaxDurability() <= 0) return;

        int unbreakingLevel = rodItemStack.getEnchantmentLevel(Enchantment.UNBREAKING);
        if (unbreakingLevel > 0) {
            if (ThreadLocalRandom.current().nextDouble() > (1.0 / (unbreakingLevel + 1))) {
                return;
            }
        }

        ItemMeta meta = rodItemStack.getItemMeta();
        if (meta instanceof Damageable damageable) {
            int newDamage = damageable.getDamage() + 1;
            if (newDamage >= rodItemStack.getType().getMaxDurability()) {
                rodItemStack.setAmount(0);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            } else {
                damageable.setDamage(newDamage);
                rodItemStack.setItemMeta(meta);
            }
        }
    }

    private void handleStandardFishing(PlayerFishEvent event, Player player, FishHook hook, BlightedPlayer blightedPlayer) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        BlightedItem rod = getLavaRodFromInventory(player);
        if (rod != null) {
            event.setCancelled(true);
            Formatter.warn(player, "Lava fishing rods cannot be used in water!");
            return;
        }

        Entity caught = event.getCaught();
        if (!(caught instanceof Item caughtItem)) return;

        if (ThreadLocalRandom.current().nextDouble() > CUSTOM_LOOT_CHANCE) return;

        World.Environment environment = player.getWorld().getEnvironment();
        FishingLootTable lootTable = FishingLootRegistry.getTable(environment, FishingMethod.WATER);

        boolean success = lootTable.roll(blightedPlayer, hook.getLocation(), caughtItem.getVelocity());
        if (success) {
            caughtItem.remove();
        }
    }
}
