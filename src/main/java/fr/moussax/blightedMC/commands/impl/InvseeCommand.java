package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.commands.AdminCommand;
import fr.moussax.blightedMC.engine.player.menus.InvSeeMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.blightedMC.utils.Formatter.warn;

public final class InvseeCommand extends AdminCommand {

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length != 1) {
            warn(player, "Usage: /invsee <player>");
            return true;
        }

        Player target = requireTarget(player, args[0]);
        if (target == null) return true;

        BlightedMC.menuManager().openMenu(new InvSeeMenu(target), player);
        return true;
    }
}
