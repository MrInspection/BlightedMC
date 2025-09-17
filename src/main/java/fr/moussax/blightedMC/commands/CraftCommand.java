package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.core.items.crafting.menu.CraftingTableMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CraftCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if(!(label.equalsIgnoreCase("craft") && sender instanceof Player player)) return false;
    player.openInventory(CraftingTableMenu.createInventory());
    return true;
  }
}
