package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem {
  private final ItemStack item;
  private final Consumer<Player> onClick;

  public ClickableItem(ItemStack item, Consumer<Player> onClick) {
    this.item = item;
    this.onClick = onClick;
  }

  public ItemStack getItem() {
    return item;
  }

  public void click(Player player) {
    this.onClick.accept(player);
  }
}
