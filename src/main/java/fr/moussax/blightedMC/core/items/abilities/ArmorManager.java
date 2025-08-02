package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class ArmorManager {
  private ArmorManager() {}

  public static void updatePlayerArmor(BlightedPlayer player) {
    List<ItemManager> equipped = getEquippedArmor(player.getPlayer());

    player.clearArmorPieces();
    Map<Class<? extends FullSetBonus>, Integer> bonusCount = new HashMap<>();

    for (ItemManager item : equipped) {
      if (item == null) continue;
      player.setArmorPiece(item.getItemType(), item);
      FullSetBonus bonus = item.getFullSetBonus();
      if (bonus != null) {
        bonusCount.merge(bonus.getClass(), 1, Integer::sum);
        Bukkit.getLogger().info("[DEBUG] Found armor piece with bonus: " + item.getItemId() + " - Bonus class: " + bonus.getClass().getSimpleName());
      }
    }

    // Check current active bonuses and stop those that no longer meet requirements
    for (FullSetBonus activeBonus : player.getActiveFullSetBonuses()) {
      Class<? extends FullSetBonus> bonusClass = activeBonus.getClass();
      int currentPieces = bonusCount.getOrDefault(bonusClass, 0);
      int requiredPieces = activeBonus.getMaxPieces();
      
      Bukkit.getLogger().info("[DEBUG] Checking active bonus: " + bonusClass.getSimpleName() + " - Current pieces: " + currentPieces + "/" + requiredPieces);
      
      if (currentPieces < requiredPieces) {
        Bukkit.getLogger().info("[DEBUG] Stopping active bonus: " + bonusClass.getSimpleName() + " - Insufficient pieces");
        player.removeActiveBonusByClass(bonusClass);
      }
    }
    
    // Start new bonuses if requirements are met
    bonusCount.forEach((bonusClass, count) -> {
      try {
        FullSetBonus setBonus = bonusClass.getDeclaredConstructor().newInstance();
        setBonus.setPlayer(player);
        Bukkit.getLogger().info("[DEBUG] Checking armor set bonus: " + bonusClass.getSimpleName() + " - Pieces: " + count + "/" + setBonus.getMaxPieces());
        // Check if the player has the required number of pieces for the full set bonus
        if (count >= setBonus.getMaxPieces()) {
          // Check if this bonus is already active
          boolean alreadyActive = false;
          for (FullSetBonus activeBonus : player.getActiveFullSetBonuses()) {
            if (activeBonus.getClass().equals(bonusClass)) {
              alreadyActive = true;
              break;
            }
          }
          
          if (!alreadyActive) {
            player.addActiveBonus(setBonus);
            setBonus.startAbility();
            Bukkit.getLogger().info("[DEBUG] Full set bonus activated: " + bonusClass.getSimpleName());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private static List<ItemManager> getEquippedArmor(Player player) {
    var armor = player.getInventory().getArmorContents();
    List<ItemManager> managers = new ArrayList<>(armor.length);
    for (ItemStack item : armor) {
      managers.add(ItemManager.fromItemStack(item));
    }
    return managers;
  }
}
