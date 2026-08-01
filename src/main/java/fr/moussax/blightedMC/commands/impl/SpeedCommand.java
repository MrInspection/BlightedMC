package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.blightedMC.utils.Formatter.*;

public final class SpeedCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            applySpeed(player, 1);
            return true;
        }

        if (args.length != 1) {
            inform(player, "Usage: /speed <1-10>");
            return true;
        }

        final int intensity;

        try {
            intensity = Integer.parseInt(args[0]);
        } catch (NumberFormatException exception) {
            warn(player, "Speed must be a value between §41 §cand §410§c.");
            return true;
        }

        if (intensity < 1 || intensity > 10) {
            warn(player, "Speed must be a value between §41 §cand §410§c.");
            return true;
        }

        applySpeed(player, intensity);
        return true;
    }

    private void applySpeed(Player player, int intensity) {
        float speed = Math.min(1.0f, (intensity + 1) / 10.0f);

        boolean flying = player.getAllowFlight() && player.isFlying();
        String type = flying ? "Flying" : "Walking";

        if (flying) {
            player.setFlySpeed(speed);
        } else {
            player.setWalkSpeed(speed);
        }

        if (intensity == 1) {
            inform(player, " §f" + type + " Speed §ehas been reset.");
            return;
        }

        text(" §f" + type + " Speed §eset to §d" + intensity + "§e. ")
                .hoverAndExecute(
                        "§4[§c➟ Reset§4]",
                        "§eClick to reset your §6" + type.toLowerCase() + " speed§e.",
                        "/speed 1"
                )
                .send(player);
    }
}
