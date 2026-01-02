package fr.moussax.blightedMC.smp.core.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.fishing.FishingLootTable;
import fr.moussax.blightedMC.smp.core.fishing.FishingMethod;
import fr.moussax.blightedMC.smp.core.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.features.items.abilities.weave.EmberWeaveSetBonus;
import fr.moussax.blightedMC.smp.features.items.abilities.weave.MagmaweaveSetBonus;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class FishingListener implements Listener {
    private static final double CUSTOM_LOOT_CHANCE = BlightedMC.getInstance().getSettings().getCustomLootChance();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            handleFishingCast(player, hook, event);
            return;
        }

        LavaFishingHook customHook = LavaFishingHook.get(hook);
        if (customHook != null) {
            handleLavaFishingReel(event, customHook, player);
            return;
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            handleStandardFishing(event, player, hook);
        }
    }

    private void handleFishingCast(Player player, FishHook hook, PlayerFishEvent event) {
        ItemStack rodStack = findFishingRodItem(player, ItemType.LAVA_FISHING_ROD);
        if (rodStack == null) return;

        if (hook.getLocation().getBlock().getType() == Material.WATER) {
            Formatter.warn(player, "Lava fishing rods cannot be used in water!");
            event.setCancelled(true);
            return;
        }

        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        double speedMultiplier = 1.0;
        if(blightedPlayer != null && blightedPlayer.hasFullSetBonus(EmberWeaveSetBonus.class)) {
            speedMultiplier = 0.85;
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 0.5f, 0.8f);
        } else if (blightedPlayer != null && blightedPlayer.hasFullSetBonus(MagmaweaveSetBonus.class)) {
            speedMultiplier = 0.70;
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 0.5f, 0.8f);
        }

        new LavaFishingHook(hook, blightedPlayer, player, rodStack, speedMultiplier);
    }

    private void handleLavaFishingReel(PlayerFishEvent event, LavaFishingHook customHook, Player player) {
        PlayerFishEvent.State state = event.getState();

        if (state == PlayerFishEvent.State.REEL_IN ||
            state == PlayerFishEvent.State.IN_GROUND ||
            state == PlayerFishEvent.State.CAUGHT_FISH) {

            if (customHook.reelIn()) {
                damageRod(player);
            }
        } else {
            customHook.remove();
        }
    }

    private void handleStandardFishing(PlayerFishEvent event, Player player, FishHook hook) {
        if (findFishingRodItem(player, ItemType.LAVA_FISHING_ROD) != null) {
            event.setCancelled(true);
            Formatter.warn(player, "Lava fishing rods cannot be used in water!");
            return;
        }

        Entity caught = event.getCaught();
        if (!(caught instanceof Item caughtItem)) return;

        if (ThreadLocalRandom.current().nextDouble() > CUSTOM_LOOT_CHANCE) return;

        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        World.Environment env = player.getWorld().getEnvironment();
        FishingLootTable lootTable = FishingLootRegistry.getTable(env, FishingMethod.WATER);

        Vector velocity = calculateVelocity(hook.getLocation(), player.getLocation());

        if (lootTable.roll(blightedPlayer, hook.getLocation(), velocity)) {
            caughtItem.remove();
        }
    }

    private ItemStack findFishingRodItem(Player player, ItemType requiredType) {
        ItemStack main = player.getInventory().getItemInMainHand();
        if (isRodMaterial(main)) {
            BlightedItem bMain = BlightedItem.fromItemStack(main);
            if (bMain != null && bMain.getItemType() == requiredType) return main;
        }

        ItemStack off = player.getInventory().getItemInOffHand();
        if (isRodMaterial(off)) {
            BlightedItem bOff = BlightedItem.fromItemStack(off);
            if (bOff != null && bOff.getItemType() == requiredType) return off;
        }

        return null;
    }

    private boolean isRodMaterial(ItemStack stack) {
        return stack != null && stack.getType() == Material.FISHING_ROD;
    }

    private void damageRod(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack rodStack = findFishingRodItem(player, ItemType.LAVA_FISHING_ROD);
        if (rodStack == null) return;

        int unbreaking = rodStack.getEnchantmentLevel(Enchantment.UNBREAKING);
        if (unbreaking > 0 && ThreadLocalRandom.current().nextInt(100) < (100 / (unbreaking + 1))) {
            return;
        }

        if (!rodStack.hasItemMeta()) return;
        ItemMeta meta = rodStack.getItemMeta();

        if (meta instanceof Damageable damageable) {
            int max = rodStack.getType().getMaxDurability();
            if (max <= 0) return;

            int newDamage = damageable.getDamage() + 1;

            if (newDamage >= max) {
                rodStack.setAmount(0);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            } else {
                damageable.setDamage(newDamage);
                rodStack.setItemMeta(meta);
            }
        }
    }

    private Vector calculateVelocity(Location origin, Location target) {
        Vector vector = target.toVector().subtract(origin.toVector());
        double distance = vector.length();

        vector.multiply(0.1);
        vector.setY(vector.getY() + Math.sqrt(distance) * 0.08);
        return vector;
    }
}
