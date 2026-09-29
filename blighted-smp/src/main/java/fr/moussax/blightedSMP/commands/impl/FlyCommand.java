package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.commands.AdminCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

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
            player.sendMessage(" §fFlight Mode §etoggled §aON§e.");
        }
    }
}
