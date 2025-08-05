package fr.moussax.blightedMC.core.menus;

import org.bukkit.entity.Player;

public class MenuManager {
  public static void openMenu(Menu menu, Player player) {
    menu.open(player);
  }

  public static void openMenu(PaginatedMenu menu, Player player) {
    menu.open(player);
  }
}
