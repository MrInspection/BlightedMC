package fr.moussax.blightedMC.core.registry.menus;

import fr.moussax.blightedMC.core.menus.Menu;
import fr.moussax.blightedMC.core.menus.ItemInteraction;
import fr.moussax.blightedMC.core.menus.MenuElementPreset;
import fr.moussax.blightedMC.core.menus.PaginatedMenu;
import fr.moussax.blightedMC.core.menus.MenuManager;
import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExampleMenu extends Menu {
  public ExampleMenu() {
    super("§bExample Main Menu", 27);
  }

  @Override
  public void build(Player player) {
    setItem(10, new ItemBuilder(Material.EMERALD, "§aGo to Submenu").toItemStack(), ItemInteraction.ANY_CLICK, (p, t) -> openSubMenu(new SubMenu()));
    setItem(11, new ItemBuilder(Material.CHEST, "§ePaginated Submenu").toItemStack(), ItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(new PaginatedSubMenu(this), p));
    setItem(12, new ItemBuilder(Material.BOOK, "§bShow Message").toItemStack(), ItemInteraction.LEFT_CLICK, (p, t) -> MessageUtils.informSender(p, "§eLeft click!"));
    setItem(12, new ItemBuilder(Material.BOOK, "§bShow Message").toItemStack(), ItemInteraction.RIGHT_CLICK, (p, t) -> MessageUtils.informSender(p, "§cRight click!"));
    setItem(14, MenuElementPreset.CLOSE_BUTTON, ItemInteraction.ANY_CLICK, (p, t) -> close());
    setItem(16, MenuElementPreset.BACK_BUTTON, ItemInteraction.ANY_CLICK, (p, t) -> goBack());
    fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
  }

  public static class SubMenu extends Menu {
    public SubMenu() {
      super("§6Submenu", 18);
    }

    @Override
    public void build(Player player) {
      setItem(4, new ItemBuilder(Material.DIAMOND, "§bBack to Main").toItemStack(), ItemInteraction.ANY_CLICK, (p, t) -> openSubMenu(new ExampleMenu()));
      setItem(8, MenuElementPreset.CLOSE_BUTTON, ItemInteraction.ANY_CLICK, (p, t) -> close());
      fillEmptyWith(MenuElementPreset.EMPTY_SLOT_FILLER);
    }
  }

  public static class PaginatedSubMenu extends PaginatedMenu {
    private final Menu previousMenu;

    public PaginatedSubMenu(Menu previousMenu) {
      super("§dPaginated Submenu", 27);
      this.previousMenu = previousMenu;
    }

    @Override
    protected int getTotalItems(Player player) {
      return 50;
    }

    @Override
    protected ItemStack getItem(Player player, int index) {
      return new ItemBuilder(Material.PAPER, "§fPage Item #" + (index + 1)).toItemStack();
    }

    @Override
    public void build(Player player) {
      super.build(player);
      // Always show the back button at size-9, even on the first page
      setItem(size - 9, MenuElementPreset.BACK_BUTTON, ItemInteraction.ANY_CLICK, (p, t) -> MenuManager.openMenu(previousMenu, p));
    }

    @Override
    protected void onItemClick(Player player, int index, org.bukkit.event.inventory.ClickType clickType) {
      MessageUtils.informSender(player, "§aClicked paginated item #" + (index + 1));
    }
  }
}