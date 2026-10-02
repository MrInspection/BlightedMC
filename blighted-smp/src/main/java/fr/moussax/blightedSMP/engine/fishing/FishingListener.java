package fr.moussax.blightedSMP.engine.fishing;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.engine.fishing.hooks.CustomFishingHook;
import fr.moussax.blightedSMP.engine.fishing.hooks.LavaFishingHook;
import fr.moussax.blightedSMP.engine.fishing.hooks.VoidFishingHook;
import fr.moussax.blightedSMP.engine.fishing.modifiers.FishingSpeedCalculator;
import fr.moussax.blightedSMP.engine.fishing.modifiers.FishingSpeedModifier;
import fr.moussax.blightedSMP.engine.fishing.registry.FishingLootRegistry;
import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.ItemType;
import fr.moussax.blightedSMP.engine.items.abilities.FullSetBonus;
import fr.moussax.blightedSMP.engine.player.BlightedPlayer;
import fr.moussax.bedrock.text.Messenger;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public final class FishingListener implements Listener {
    private static final double CUSTOM_LOOT_CHANCE = BlightedSMP.getInstance().getSettings().getCustomLootChance();

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        FishHook hook = event.getHook();

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            handleFishingCast(player, hook, event);
            return;
        }

        CustomFishingHook customHook = CustomFishingHook.get(hook);
        if (customHook != null) {
            handleCustomFishingReel(event, player, customHook);
            return;
        }

        if (event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT
                || event.getState() == PlayerFishEvent.State.IN_GROUND
                || event.getState() == PlayerFishEvent.State.REEL_IN) {
            FishingComboTracker.resetCombo(player, FishingMethod.WATER);
            return;
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            handleStandardFishing(event, player, hook);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        FishingComboTracker.clear(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        FishingComboTracker.clear(event.getPlayer());
    }

    private void handleFishingCast(Player player, FishHook hook, PlayerFishEvent event) {
        ItemStack lavaRod = findFishingRodItem(player, ItemType.LAVA_FISHING_ROD);
        if (lavaRod != null) {
            if (hook.getLocation().getBlock().getType() == Material.WATER) {
                Messenger.warn(player, "This rod thirsts for molten depths, not ordinary waters.");
                event.setCancelled(true);
            } else {
                BlightedPlayer blightedPlayer = BlightedPlayer.get(player);
                new LavaFishingHook(
                        hook,
                        blightedPlayer,
                        player,
                        lavaRod,
                        resolveFishingSpeed(blightedPlayer, lavaRod)
                );
            }
            return;
        }

        ItemStack voidRod = findFishingRodItem(player, ItemType.VOID_FISHING_ROD);
        if (voidRod != null) {
            if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
                Messenger.warn(player, "This rod answers only to the void of the End.");
                event.setCancelled(true);
            } else {
                BlightedPlayer blightedPlayer = BlightedPlayer.get(player);
                new VoidFishingHook(
                        hook,
                        blightedPlayer,
                        player,
                        voidRod,
                        resolveFishingSpeed(blightedPlayer, voidRod)
                );
            }
        }
    }

    /**
     * Calculates the Fishing Speed for a cast and triggers feedback from
     * any active set bonus that contributes to the stat.
     *
     * @param blightedPlayer the player's BlightedMC state
     * @param rod            the fishing rod being used
     * @return the resolved Fishing Speed
     */
    private double resolveFishingSpeed(BlightedPlayer blightedPlayer, ItemStack rod) {
        if (blightedPlayer != null) {
            for (FullSetBonus bonus : blightedPlayer.getActiveFullSetBonuses()) {
                if (bonus instanceof FishingSpeedModifier modifier) {
                    modifier.onFishingCast(blightedPlayer.getPlayer());
                }
            }
        }

        return FishingSpeedCalculator.calculate(blightedPlayer, rod);
    }

    private void handleCustomFishingReel(
            PlayerFishEvent event,
            Player player,
            CustomFishingHook customHook
    ) {
        PlayerFishEvent.State state = event.getState();

        if (state == PlayerFishEvent.State.REEL_IN
                || state == PlayerFishEvent.State.IN_GROUND
                || state == PlayerFishEvent.State.CAUGHT_FISH) {

            if (customHook.reelIn()) {
                damageRod(player, customHook.getRequiredRodType());
            }
        } else {
            customHook.remove();
        }
    }

    private void handleStandardFishing(PlayerFishEvent event, Player player, FishHook hook) {
        if (findFishingRodItem(player, ItemType.LAVA_FISHING_ROD) != null) {
            event.setCancelled(true);
            Messenger.warn(player, "This rod thirsts for molten depths, not ordinary waters.");
            return;
        }

        if (findFishingRodItem(player, ItemType.VOID_FISHING_ROD) != null) {
            event.setCancelled(true);
            return;
        }

        World.Environment environment = player.getWorld().getEnvironment();
        if (environment == World.Environment.THE_END) return;

        Entity caught = event.getCaught();
        if (!(caught instanceof Item caughtItem)) return;

        int currentCombo = FishingComboTracker.getCombo(player, FishingMethod.WATER);

        if (ThreadLocalRandom.current().nextDouble() <= CUSTOM_LOOT_CHANCE) {
            BlightedPlayer blightedPlayer = BlightedPlayer.get(player);
            FishingLootTable lootTable = FishingLootRegistry.getTable(environment, FishingMethod.WATER);

            Vector velocity = calculateVelocity(hook.getLocation(), player.getLocation());
            int luckLevel = resolveVanillaLuckLevel(player);

            if (lootTable.roll(blightedPlayer, hook.getLocation(), velocity, luckLevel, currentCombo)) {
                caughtItem.remove();
            }
        }

        FishingComboTracker.incrementCombo(player, FishingMethod.WATER);
        int newCombo = FishingComboTracker.getCombo(player, FishingMethod.WATER);
        FishingComboTracker.spawnBonusExperience(player.getWorld(), player.getLocation(), newCombo);
    }

    private ItemStack getHeldFishingRodStack(Player player) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (isRodMaterial(mainHandItem)) return mainHandItem;

        ItemStack offhandItem = player.getInventory().getItemInOffHand();
        if (isRodMaterial(offhandItem)) return offhandItem;

        return null;
    }

    private ItemStack findFishingRodItem(Player player, ItemType requiredType) {
        ItemStack rodStack = getHeldFishingRodStack(player);
        if (rodStack != null) {
            BlightedItem blightedItem = BlightedItem.fromItemStack(rodStack);
            if (blightedItem != null && blightedItem.getItemType() == requiredType) {
                return rodStack;
            }
        }
        return null;
    }

    private int resolveVanillaLuckLevel(Player player) {
        ItemStack rodStack = getHeldFishingRodStack(player);
        return rodStack != null ? rodStack.getEnchantmentLevel(Enchantment.LUCK_OF_THE_SEA) : 0;
    }

    private boolean isRodMaterial(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == Material.FISHING_ROD;
    }

    private void damageRod(Player player, ItemType type) {
        if (player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack rodStack = findFishingRodItem(player, type);
        if (rodStack == null) return;

        int unbreakingLevel = rodStack.getEnchantmentLevel(Enchantment.UNBREAKING);
        if (unbreakingLevel > 0 && ThreadLocalRandom.current().nextInt(100) >= (100 / (unbreakingLevel + 1))) {
            return;
        }

        if (!rodStack.hasItemMeta()) return;
        ItemMeta itemMeta = rodStack.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            int maxDurability = rodStack.getType().getMaxDurability();
            if (maxDurability <= 0) return;

            int newDamage = damageable.getDamage() + 1;

            if (newDamage >= maxDurability) {
                rodStack.setAmount(0);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            } else {
                damageable.setDamage(newDamage);
                rodStack.setItemMeta(itemMeta);
            }
        }
    }

    private Vector calculateVelocity(Location origin, Location target) {
        Vector velocity = target.toVector().subtract(origin.toVector());
        double distance = velocity.length();

        velocity.multiply(0.1);
        velocity.setY(velocity.getY() + Math.sqrt(distance) * 0.08);

        return velocity;
    }
}
