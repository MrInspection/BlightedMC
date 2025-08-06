package fr.moussax.blightedMC.commands.impl.admin;

import fr.moussax.blightedMC.commands.CommandArgument;
import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.ItemsRegistryMenu;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.core.menus.MenuManager;
import fr.moussax.blightedMC.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

@CommandArgument(suggestions = {"$items"})
public class GiveItemCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!label.equalsIgnoreCase("giveitem") || !(sender instanceof Player player)) return false;
    MessageUtils.enforceAdminPermission(player);

    if (args.length == 0) {
      MenuManager.openMenu(new ItemsRegistryMenu.ItemCategoriesMenu(), player);
      return true;
    }

    String itemId = args[0].toUpperCase();
    ItemManager item = ItemsRegistry.BLIGHTED_ITEMS.get(itemId);
    if (item == null) {
      MessageUtils.warnSender(player, "Unable to find item matching the ID: §4" + itemId +"§c.");
      return false;
    }

    int amount = 1;
    if (args.length > 1) {
      try {
        amount = Math.max(1, Integer.parseInt(args[1]));
      } catch (NumberFormatException e) {
        MessageUtils.warnSender(player, "Please provide a valid amount. Provided: §4" + args[1]);
        return false;
      }
    }

    ItemStack stack = item.toItemStack().clone();
    stack.setAmount(amount);
    player.getInventory().addItem(stack);
    MessageUtils.informSender(player, "You receive §5" + amount + "x §d" + item.getItemId() + "§7.");
    return true;
  }
}
