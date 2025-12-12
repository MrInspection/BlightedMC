package fr.moussax.blightedMC.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class KaboomCommand implements CommandExecutor {
    private static final int LIGHTNING_STRIKE_AMOUNT = 10;
    private static final float LAUNCH_HEIGHT = 10.55f;

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, String label, String @NonNull [] args) {
        if (!(label.equalsIgnoreCase("kaboom") && sender instanceof Player player)) return false;
        if (!enforceAdminPermission(player)) return false;

        if (args.length == 0) {
            return launchAllPlayers(player);
        } else {
            return launchTargetedPlayer(player, args[0]);
        }
    }

    private boolean launchAllPlayers(Player commandSender) {
        World world = commandSender.getWorld();

        for (Player target : world.getPlayers()) {
            inform(commandSender, "Launched §d" + target.getName() + " §7into the sky!");
            launchPlayer(target);
            strikeLightning(world, target.getLocation());
        }
        return true;
    }

    private boolean launchTargetedPlayer(Player commandSender, String targetedPlayer) {
        Player target = Bukkit.getPlayer(targetedPlayer);
        if (target == null) {
            warn(commandSender, "Unable to find the player §4" + targetedPlayer + "§c.");
            return false;
        }

        inform(commandSender, "You launched §d" + target.getName() + "§7 into the sky!");
        launchPlayer(target);
        strikeLightning(target.getWorld(), target.getLocation());
        return true;
    }

    private void launchPlayer(Player target) {
        Vector velocity = new Vector(0, LAUNCH_HEIGHT, 0);
        target.setVelocity(velocity);
    }

    private void strikeLightning(World world, Location location) {
        for (int i = 0; i < LIGHTNING_STRIKE_AMOUNT; i++) {
            world.strikeLightning(location);
        }
    }
}
