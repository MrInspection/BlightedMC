package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.commands.AdminCommand;
import fr.moussax.blightedSMP.engine.player.menus.InvSeeMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.warn;

public final class InvseeCommand extends AdminCommand {

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length != 1) {
            warn(player, "Usage: /invsee <player>");
            return true;
        }

        Player target = requireTarget(player, args[0]);
        if (target == null) return true;

        new InvSeeMenu(target).open(player);
        return true;
    }
}
