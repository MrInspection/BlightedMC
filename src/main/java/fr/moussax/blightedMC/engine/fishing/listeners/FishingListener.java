package fr.moussax.blightedMC.engine.fishing.listeners;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.fishing.FishingLootTable;
import fr.moussax.blightedMC.engine.fishing.FishingMethod;
import fr.moussax.blightedMC.engine.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.ItemType;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.content.items.abilities.weave.EmberWeaveSetBonus;
import fr.moussax.blightedMC.content.items.abilities.weave.MagmaweaveSetBonus;
import fr.moussax.blightedMC.utils.Formatter;
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

public final class FishingListener implements Listener {
    private static final double CUSTOM_LOOT_CHANCE = BlightedMC.getInstance().getSettings().getCustomLootChance();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            handleFishingCast(player, hook, event);
            return;
        }

        LavaFishingHook lavaHook = LavaFishingHook.get(hook);
        if (lavaHook != null) {
            handleCustomFishingReel(event, player, lavaHook::reelIn, lavaHook::remove, ItemType.LAVA_FISHING_ROD);
            return;
        }

        VoidFishingHook voidHook = VoidFishingHook.get(hook);
        if (voidHook != null) {
            handleCustomFishingReel(event, player, voidHook::reelIn, voidHook::remove, ItemType.VOID_FISHING_ROD);
            return;
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            handleStandardFishing(event, player, hook);
        }
    }

    private void handleFishingCast(Player player, FishHook hook, PlayerFishEvent event) {
        ItemStack lavaRod = findFishingRodItem(player, ItemType.LAVA_FISHING_ROD);
        if (lavaRod != null) {
            if (hook.getLocation().getBlock().getType() == Material.WATER) {
                Formatter.warn(player, "This rod thirsts for molten depths, not ordinary waters.");
                event.setCancelled(true);
            } else {
                new LavaFishingHook(hook, BlightedPlayer.getBlightedPlayer(player), player, lavaRod, getSpeedMultiplier(player));
            }
            return;
        }

        ItemStack voidRod = findFishingRodItem(player, ItemType.VOID_FISHING_ROD);
        if (voidRod != null) {
            if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
                Formatter.warn(player, "This rod answers only to the void of the End.");
                event.setCancelled(true);
            } else {
                new VoidFishingHook(hook, BlightedPlayer.getBlightedPlayer(player), player, voidRod, getSpeedMultiplier(player));
            }
        }
    }

    private void handleCustomFishingReel(PlayerFishEvent event, Player player, CustomReelAction reelAction, Runnable removeAction, ItemType rodType) {
        PlayerFishEvent.State state = event.getState();

        if (state == PlayerFishEvent.State.REEL_IN ||
                state == PlayerFishEvent.State.IN_GROUND ||
                state == PlayerFishEvent.State.CAUGHT_FISH) {

            if (reelAction.reelIn()) {
                damageRod(player, rodType);
            }
        } else {
            removeAction.run();
        }
    }

    private void handleStandardFishing(PlayerFishEvent event, Player player, FishHook hook) {
        if (findFishingRodItem(player, ItemType.LAVA_FISHING_ROD) != null) {
            event.setCancelled(true);
            Formatter.warn(player, "This rod thirsts for molten depths, not ordinary waters.");
            return;
        }

        if (findFishingRodItem(player, ItemType.VOID_FISHING_ROD) != null) {
            event.setCancelled(true);
            return;
        }

        World.Environment env = player.getWorld().getEnvironment();
        if (env == World.Environment.THE_END) {
            return;
        }

        Entity caught = event.getCaught();
        if (!(caught instanceof Item caughtItem)) return;
        if (ThreadLocalRandom.current().nextDouble() > CUSTOM_LOOT_CHANCE) return;

        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
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

    private void damageRod(Player player, ItemType type) {
        if (player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack rodStack = findFishingRodItem(player, type);
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

    private double getSpeedMultiplier(Player player) {
        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
        if (blightedPlayer == null) return 1.0;

        if (blightedPlayer.hasFullSetBonus(EmberWeaveSetBonus.class)) {
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 0.5f, 0.8f);
            return 0.85;
        }
        if (blightedPlayer.hasFullSetBonus(MagmaweaveSetBonus.class)) {
            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 0.5f, 0.8f);
            return 0.70;
        }
        return 1.0;
    }

    private Vector calculateVelocity(Location origin, Location target) {
        Vector vector = target.toVector().subtract(origin.toVector());
        double distance = vector.length();

        vector.multiply(0.1);
        vector.setY(vector.getY() + Math.sqrt(distance) * 0.08);
        return vector;
    }

    @FunctionalInterface
    private interface CustomReelAction {
        boolean reelIn();
    }
}
