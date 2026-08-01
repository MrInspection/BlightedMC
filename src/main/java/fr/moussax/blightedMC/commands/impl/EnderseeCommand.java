package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.commands.AdminCommand;
import fr.moussax.blightedMC.engine.player.menus.EnderSeeMenu;
import fr.moussax.blightedMC.utils.Formatter;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class EnderseeCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            Formatter.warn(player, "Usage: /endersee <player>");
            return false;
        }

        Player target = requireTarget(player, args[0]);
        if (target == null) return false;

        BlightedMC.menuManager().openMenu(new EnderSeeMenu(target), player);
        return true;
    }
}
