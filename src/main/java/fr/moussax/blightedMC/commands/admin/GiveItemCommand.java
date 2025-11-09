package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.utils.commands.CommandArgument;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.registry.ItemDirectoryMenu;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import fr.moussax.blightedMC.core.menus.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

@CommandArgument(suggestions = {"$items"})
public class GiveItemCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!label.equalsIgnoreCase("giveitem") || !(sender instanceof Player player)) return false;
    enforceAdminPermission(player);

    if (args.length == 0) {
      MenuManager.openMenu(new ItemDirectoryMenu.ItemCategoriesMenu(), player);
      return true;
    }

    String itemId = args[0].toUpperCase();
    ItemTemplate itemTemplate = ItemDirectory.getItem(itemId);
    if (itemTemplate == null) {
      warn(player, "Unable to find item matching the ID: §4" + itemId +"§c.");
      return false;
    }

    int amount = 1;
    if (args.length > 1) {
      try {
        amount = Math.max(1, Integer.parseInt(args[1]));
      } catch (NumberFormatException e) {
        warn(player, "Please provide a valid amount. Provided: §4" + args[1]);
        return false;
      }
    }

    ItemStack stack = itemTemplate.toItemStack().clone();
    stack.setAmount(amount);
    player.getInventory().addItem(stack);
    inform(player, "You receive §5" + amount + "x §d" + itemTemplate.getItemId() + "§7.");
    return true;
  }
}
