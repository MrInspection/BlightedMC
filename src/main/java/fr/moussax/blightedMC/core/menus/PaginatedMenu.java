package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedMenu extends Menu {
  protected int currentPage = 0;
  protected List<Inventory> pages = new ArrayList<>();

  public PaginatedMenu(String title, int size) {
    super(title, size);
  }

  @Override
  public void open(Player player){
    player.openInventory(pages.get(currentPage));
  }
}
