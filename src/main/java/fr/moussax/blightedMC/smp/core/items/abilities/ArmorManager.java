package fr.moussax.blightedMC.smp.core.items.abilities;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ArmorManager {

    private ArmorManager() {
    }

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
                try {
                    active.deactivate();
                } catch (Exception e) {
                    Log.error("ArmorManager", "Error stopping ability: " + active.getClass().getSimpleName());
                }
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

                if (newBonus.getType() == FullSetBonus.SetType.NORMAL) {
                    newBonus.activate();
                } else if (newBonus.getType() == FullSetBonus.SetType.SNEAK && player.getPlayer().isSneaking()) {
                    newBonus.activate();
                }
            } catch (Exception e) {
                Log.error("ArmorManager", "Failed to activate bonus " + bonusClass.getSimpleName());
            }
        });
    }

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
