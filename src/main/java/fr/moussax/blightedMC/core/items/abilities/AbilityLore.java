package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the lore of an item ability.
 * Each ability can contribute lines to the item's lore.
 */
public class AbilityLore {

  private final List<String> loreLines = new ArrayList<>();

  public AbilityLore addLine(String line) {
    loreLines.add(line);
    return this;
  }

  /**
   * Generates the lore for this ability.
   *
   * @param player Optional player context (can be null)
   * @param item   The item this lore is attached to
   * @return A list of formatted lore lines
   */
  public List<String> makeLore(Player player, ItemStack item) {
    // In the future, you could generate dynamic lore based on player or item stats
    return new ArrayList<>(loreLines);
  }
}
