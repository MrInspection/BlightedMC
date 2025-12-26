package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.smp.core.items.forging.menu.ForgeMenu;
import fr.moussax.blightedMC.smp.core.menus.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class ForgeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if(!(label.equalsIgnoreCase("forge") && sender instanceof Player player)) return false;
        MenuManager.openMenu(new ForgeMenu(null), player);
        return true;
    }
}
