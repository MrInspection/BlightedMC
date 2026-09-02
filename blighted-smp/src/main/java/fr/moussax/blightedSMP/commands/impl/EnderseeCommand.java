package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.commands.AdminCommand;
import fr.moussax.blightedSMP.engine.player.menus.EnderSeeMenu;
import fr.moussax.bedrock.text.Messenger;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class EnderseeCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            Messenger.warn(player, "Usage: /endersee <player>");
            return false;
        }

        Player target = requireTarget(player, args[0]);
        if (target == null) return false;

        BlightedSMP.menuManager().openMenu(new EnderSeeMenu(target), player);
        return true;
    }
}
