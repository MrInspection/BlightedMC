package fr.moussax.blightedMC.smp.core.items.abilities;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.debug.Log;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ArmorManager {

    private ArmorManager() {
    }

    public static void updatePlayerArmor(BlightedPlayer player) {
        ItemStack[] armorContents = player.getPlayer().getInventory().getArmorContents();
        player.clearArmorPieces();

        Map<Class<? extends FullSetBonus>, Integer> bonusCount = new HashMap<>();

        for (ItemStack item : armorContents) {
            if (item == null) continue;
            if (item.getType().isAir()) continue;

            BlightedItem blightedItem = BlightedItem.fromItemStack(item);
            if (blightedItem == null) continue;

            player.setArmorPiece(blightedItem.getItemType(), blightedItem);

            FullSetBonus bonus = blightedItem.getFullSetBonus();
            if (bonus == null) continue;

            bonusCount.merge(bonus.getClass(), 1, Integer::sum);
        }

        for (FullSetBonus activeBonus : new ArrayList<>(player.getActiveFullSetBonuses())) {
            int count = bonusCount.getOrDefault(activeBonus.getClass(), 0);
            if (count >= activeBonus.getMaxPieces()) continue;

            activeBonus.deactivate();
            player.removeActiveBonusByClass(activeBonus.getClass());
        }

        bonusCount.forEach((bonusClass, count) -> {
            try {
                for (FullSetBonus active : player.getActiveFullSetBonuses()) {
                    if (active.getClass().equals(bonusClass)) {
                        return;
                    }
                }

                FullSetBonus tempInstance = bonusClass.getDeclaredConstructor().newInstance();
                if (count < tempInstance.getMaxPieces()) return;

                tempInstance.setPlayer(player);
                player.addActiveBonus(tempInstance);

                if (tempInstance.getType() == FullSetBonus.SetType.NORMAL) {
                    tempInstance.activate();
                    return;
                }

                if (tempInstance.getType() == FullSetBonus.SetType.SNEAK
                    && player.getPlayer().isSneaking()) {
                    tempInstance.activate();
                }
            } catch (Exception e) {
                Log.error(
                    "ArmorManager",
                    "Failed to activate full set bonus " + bonusClass.getSimpleName()
                );
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
