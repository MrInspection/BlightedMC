package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
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
        List<ItemTemplate> equipped = getEquippedArmor(player.getPlayer());

        player.clearArmorPieces();
        Map<Class<? extends FullSetBonus>, Integer> bonusCount = new HashMap<>();

        for (ItemTemplate itemTemplate : equipped) {
            if (itemTemplate == null) continue;
            player.setArmorPiece(itemTemplate.getItemType(), itemTemplate);
            FullSetBonus bonus = itemTemplate.getFullSetBonus();
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

    private static List<ItemTemplate> getEquippedArmor(Player player) {
        var armor = player.getInventory().getArmorContents();
        List<ItemTemplate> managers = new ArrayList<>(armor.length);
        for (ItemStack item : armor) {
            if (item == null || item.getType().isAir()) continue;
            ItemTemplate manager = ItemTemplate.fromItemStack(item);
            if (manager != null) managers.add(manager);
        }
        return managers;
    }
}
