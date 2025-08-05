package fr.moussax.blightedMC.core.items.abilities;

import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Handles player armor management and the activation or removal of full set bonuses.
 * <p>
 * This class is responsible for:
 * <ul>
 *   <li>Tracking equipped armor pieces and their {@link ItemManager} instances.</li>
 *   <li>Updating a {@link BlightedPlayer}'s armor inventory and full set bonuses.</li>
 *   <li>Activating and deactivating {@link FullSetBonus} effects based on equipped items.</li>
 * </ul>
 */
public final class ArmorManager {
  private ArmorManager() {}

  /**
   * Updates the armor state of a {@link BlightedPlayer} and manages their full set bonuses.
   * <p>
   * The process includes:
   * <ul>
   *   <li>Refreshing the player's equipped armor pieces.</li>
   *   <li>Counting pieces that contribute to full set bonuses.</li>
   *   <li>Removing bonuses that no longer meet the required piece count.</li>
   *   <li>Starting new bonuses if the player equips the full set.</li>
   * </ul>
   *
   * @param player the {@link BlightedPlayer} whose armor state should be updated
   */
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
      }
    }

    // Check current active bonuses and stop those that no longer meet requirements
    for (FullSetBonus activeBonus : player.getActiveFullSetBonuses()) {
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
            setBonus.startAbility();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Retrieves a list of {@link ItemManager} instances for all armor pieces a player has equipped.
   *
   * @param player the Bukkit player
   * @return a list of {@link ItemManager} for equipped armor pieces, ignoring empty slots
   */
  private static List<ItemManager> getEquippedArmor(Player player) {
    var armor = player.getInventory().getArmorContents();
    List<ItemManager> managers = new ArrayList<>(armor.length);
    for (ItemStack item : armor) {
      if (item == null || item.getType().isAir()) continue;
      ItemManager manager = ItemManager.fromItemStack(item);
      if (manager != null) managers.add(manager);
    }
    return managers;
  }
}
