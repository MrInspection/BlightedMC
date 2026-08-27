package fr.moussax.blightedMC.engine.items.abilities;

import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages player armor evaluation, set bonus activation, and sneak state updates.
 */
public final class ArmorManager {

    private ArmorManager() {
    }

    /**
     * Inspects a player's equipped armor contents and updates active set bonuses.
     *
     * @param player player context to evaluate
     */
    public static void updatePlayerArmor(BlightedPlayer player) {
        ItemStack[] armorContents = player.getPlayer().getInventory().getArmorContents();
        player.clearArmorPieces();

        Map<Class<? extends FullSetBonus>, Integer> bonusCount = new HashMap<>();

        for (ItemStack item : armorContents) {
            if (item == null || item.getType().isAir()) continue;

            BlightedItem blightedItem = BlightedItem.fromItemStack(item);
            if (blightedItem == null) continue;

            player.setArmorPiece(blightedItem.getItemType(), blightedItem);

            FullSetBonus bonus = blightedItem.getFullSetBonus();
            if (bonus != null) {
                bonusCount.merge(bonus.getClass(), 1, Integer::sum);
            }
        }

        List<FullSetBonus> activeBonuses = new ArrayList<>(player.getActiveFullSetBonuses());

        for (FullSetBonus active : activeBonuses) {
            int equippedCount = bonusCount.getOrDefault(active.getClass(), 0);

            if (equippedCount < active.getMaxPieces()) {
                player.removeActiveBonusByClass(active.getClass());
            }
        }

        bonusCount.forEach((bonusClass, count) -> {
            boolean isRunning = player.getActiveFullSetBonuses().stream()
                    .anyMatch(b -> b.getClass().equals(bonusClass));

            if (isRunning) return;

            try {
                FullSetBonus newBonus = bonusClass.getDeclaredConstructor().newInstance();
                if (count < newBonus.getMaxPieces()) return;

                newBonus.setPlayer(player);
                player.addActiveBonus(newBonus);
            } catch (Exception exception) {
                Log.error("ArmorManager", "Failed to activate bonus " + bonusClass.getSimpleName());
            }
        });
    }

    /**
     * Toggles sneak-dependent set bonuses when a player changes sneak state.
     *
     * @param player player context
     * @param isSneaking {@code true} if player started sneaking, {@code false} if stopped
     */
    public static void handleSneakUpdate(BlightedPlayer player, boolean isSneaking) {
        for (FullSetBonus bonus : player.getActiveFullSetBonuses()) {
            if (bonus.getType() != FullSetBonus.SetType.SNEAK) continue;

            if (isSneaking) {
                bonus.activate();
            } else {
                bonus.deactivate();
            }
        }
    }
}
