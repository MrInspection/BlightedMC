package fr.moussax.blightedMC.smp.core.items.abilities;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ArmorManager {
    private ArmorManager() {
    }

    public static void updatePlayerArmor(BlightedPlayer player) {
        List<BlightedItem> equipped = getEquippedArmor(player.getPlayer());

        player.clearArmorPieces();
        Map<Class<? extends FullSetBonus>, Integer> bonusCount = new HashMap<>();

        for (BlightedItem blightedItem : equipped) {
            if (blightedItem == null) continue;
            player.setArmorPiece(blightedItem.getItemType(), blightedItem);
            FullSetBonus bonus = blightedItem.getFullSetBonus();
            if (bonus != null) {
                bonusCount.merge(bonus.getClass(), 1, Integer::sum);
            }
        }

        // Check current active bonuses and stop those that no longer meet requirements
        for (FullSetBonus activeBonus : new ArrayList<>(player.getActiveFullSetBonuses())) {
            Class<? extends FullSetBonus> bonusClass = activeBonus.getClass();
            int currentPieces = bonusCount.getOrDefault(bonusClass, 0);
            int requiredPieces = activeBonus.getMaxPieces();
            if (currentPieces < requiredPieces) {
                player.removeActiveBonusByClass(bonusClass);
            }
        }

        // Start new bonuses if requirements are met
        bonusCount.forEach((bonusClass, count) -> {
            try {
                FullSetBonus setBonus = bonusClass.getDeclaredConstructor().newInstance();
                setBonus.setPlayer(player);
                // Check if the player has the required number of pieces for the full set bonus
                if (count >= setBonus.getMaxPieces()) {
                    boolean alreadyActive = false;
                    for (FullSetBonus activeBonus : player.getActiveFullSetBonuses()) {
                        if (activeBonus.getClass().equals(bonusClass)) {
                            alreadyActive = true;
                            break;
                        }
                    }

                    if (!alreadyActive) {
                        player.addActiveBonus(setBonus);
                        setBonus.activate();
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to activate full set bonus " + bonusClass.getSimpleName());
            }
        });
    }

    private static List<BlightedItem> getEquippedArmor(Player player) {
        var armor = player.getInventory().getArmorContents();
        List<BlightedItem> managers = new ArrayList<>(armor.length);
        for (ItemStack item : armor) {
            if (item == null || item.getType().isAir()) continue;
            BlightedItem manager = BlightedItem.fromItemStack(item);
            if (manager != null) managers.add(manager);
        }
        return managers;
    }
}
