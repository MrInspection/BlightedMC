package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.listeners.BlightedEntitiesListener;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import static fr.moussax.blightedMC.utils.Formatter.warn;

public final class ButcherCommand extends AdminCommand {

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length > 1) {
            warn(player, "Usage: /butcher [radius]");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(" §eRemoved §d" + butcher(player) + " §eentities.");
            return true;
        }

        final double radius;

        try {
            radius = Double.parseDouble(args[0]);
        } catch (NumberFormatException exception) {
            warn(player, "Radius must be a valid number.");
            return true;
        }

        if (radius <= 0) {
            warn(player, "Radius must be greater than 0.");
            return true;
        }

        player.sendMessage(" §eRemoved §d" + butcher(player, radius) + " §eentities in a §b" + Math.round(radius) + " §eblocks radius.");
        return true;
    }

    private int butcher(Player player) {
        int removed = 0;

        for (LivingEntity entity : player.getWorld().getLivingEntities()) {
            if (entity instanceof Player) continue;

            removeEntity(entity);
            removed++;
        }

        return removed;
    }

    private int butcher(Player player, double radius) {
        int removed = 0;
        Location origin = player.getLocation();
        double radiusSquared = radius * radius;

        for (LivingEntity entity : player.getWorld().getLivingEntities()) {
            if (entity instanceof Player) continue;

            if (entity.getLocation().distanceSquared(origin) > radiusSquared) {
                continue;
            }

            removeEntity(entity);
            removed++;
        }

        return removed;
    }

    private void removeEntity(LivingEntity entity) {
        BlightedEntity blighted = BlightedEntitiesListener.getBlightedEntity(entity);
        if (blighted != null) {
            blighted.cleanup();
        }
        if (!entity.isDead()) {
            entity.remove();
        }
    }
}
