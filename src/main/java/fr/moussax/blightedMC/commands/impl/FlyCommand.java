package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.blightedMC.shared.text.InteractiveMessage.text;

public final class FlyCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        toggleFlightMode(player);
        return true;
    }

    private void toggleFlightMode(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.sendMessage(" §fFlight Mode §etoggled §cOFF§e.");
        } else {
            player.setAllowFlight(true);
            player.setFlying(true);
            text(" §fFlight Mode §etoggled §aON§e. ").hoverAndExecute("§4[§c➟ Disable§4]", "§eClick to disable your §6Flight Mode§e.", "/fly").send(player);
        }
    }
}
