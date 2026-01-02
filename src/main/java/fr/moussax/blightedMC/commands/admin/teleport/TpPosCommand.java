package fr.moussax.blightedMC.commands.admin.teleport;

import fr.moussax.blightedMC.utils.commands.CommandArgument;
import fr.moussax.blightedMC.utils.commands.CommandArguments;
import fr.moussax.blightedMC.utils.commands.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

@CommandArguments({
    @CommandArgument(suggestions = {"$players"}),
    @CommandArgument(position = 3, suggestions = {"OVERWORLD", "NETHER", "THE_END"}),
    @CommandArgument(position = 4, suggestions = {"OVERWORLD", "NETHER", "THE_END"})
})
public class TpPosCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!enforceAdminPermission(player)) return false;

        if (args.length < 3) {
            CommandInfo.sendUsage(player,
                "Teleport to coordinates. World is optional.",
                "tppos", "[blightedPlayer]", "<x>", "<y>", "<z>", "[world]"
            );
            return true;
        }

        try {
            Player target = player;
            int coordIndex = 0;

            Player potentialTarget = Bukkit.getPlayerExact(args[0]);
            if (potentialTarget != null) {
                target = potentialTarget;
                coordIndex = 1;
            }

            if (args.length < coordIndex + 3) {
                warn(player, "Missing arguments! Please provide coordinates (x y z).");
                return false;
            }

            double x = Double.parseDouble(args[coordIndex]);
            double y = Double.parseDouble(args[coordIndex + 1]);
            double z = Double.parseDouble(args[coordIndex + 2]);

            World world = target.getWorld();
            if (args.length > coordIndex + 3) {
                String worldName = args[coordIndex + 3];
                world = parseWorld(worldName);
                if (world == null) {
                    warn(player, "Invalid world: " + worldName + ". Use OVERWORLD, NETHER, or THE_END.");
                    return false;
                }
            }

            Location location = new Location(world, x, y, z);
            target.teleport(location);

            String displayWorld = getWorldDisplayName(world);
            String coordinates = String.format("§d%.1f, %.1f, %.1f §7in §5%s§7.", x, y, z, displayWorld);

            if (target.equals(player)) {
                inform(player, "§7Teleported to " + coordinates);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.75f);
            } else {
                inform(player, "§7Teleported §f" + target.getName() + " §7to " + coordinates);
                inform(target, "§7Teleported to " + coordinates);
                player.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.75f);
            }
        } catch (NumberFormatException e) {
            warn(player, "Invalid coordinates. Please provide valid numbers.");
            return false;
        }
        return true;
    }

    private World parseWorld(String name) {
        return switch (name.toUpperCase()) {
            case "NETHER" -> Bukkit.getWorld("world_nether");
            case "THE_END" -> Bukkit.getWorld("world_the_end");
            case "OVERWORLD" -> Bukkit.getWorld("world");
            default -> Bukkit.getWorld(name);
        };
    }

    private String getWorldDisplayName(World world) {
        return switch (world.getEnvironment()) {
            case NETHER -> "Nether";
            case THE_END -> "The End";
            default -> "Overworld";
        };
    }
}
