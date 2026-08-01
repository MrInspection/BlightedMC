package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class TestCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        player.sendMessage(" §eGave §d25 §egems to §fTrixmas§e.");
        player.sendMessage(" §eYou gave all players §d25 §egems.");
        player.sendMessage(" §eYou reset all players' gems balance.");
        player.sendMessage("");
        player.sendMessage(" §7You received §d25 §7gems.");
        player.sendMessage(" §7Your gems balance has been §creset§e.");
        player.sendMessage(" §7Your gems balance was set to §d67 §7gems.");
        player.sendMessage("");
        return true;
    }
}
