package fr.moussax.blightedMC.core.entities.LootTable;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ItemLoot implements DroppableConsumable {
  private final ItemStack itemStack;
  private final int minAmount;
  private final int maxAmount;

  public ItemLoot(ItemStack itemStack, int minAmount, int maxAmount) {
    if (minAmount < 1 || maxAmount < minAmount) {
      throw new IllegalArgumentException("Invalid min/max amounts");
    }
    this.itemStack = itemStack;
    this.minAmount = minAmount;
    this.maxAmount = maxAmount;
  }

  /**
   * Generates a random amount for this loot drop.
   */
  public int generateAmount() {
    return minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
  }

  /**
   * Drops the loot at the location or to the player's inventory.
   * @param amount pre-generated amount to ensure consistent messaging
   */
  public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer, int amount) {
    ItemStack drop = itemStack.clone();
    drop.setAmount(amount);

    if (toPlayer && killer != null && killer.getPlayer() != null) {
      killer.getPlayer().getInventory().addItem(drop);
    } else {
      Objects.requireNonNull(dropLocation.getWorld()).dropItemNaturally(dropLocation, drop);
    }
  }

  @Override
  public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer) {
    int amount = generateAmount();
    consume(killer, dropLocation, toPlayer, amount);
  }

  @Override
  public String name() {
    ItemMeta meta = itemStack.getItemMeta();
    if(meta != null && meta.hasDisplayName()) {
      return meta.getDisplayName();
    }
    String rawName = itemStack.getType().name().toLowerCase().replace('_', ' ');
    return Character.toUpperCase(rawName.charAt(0)) + rawName.substring(1);
  }
}
