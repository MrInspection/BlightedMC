package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.utils.formatting.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class TeleportCommands implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        switch (sender) {
            case Player player when label.equalsIgnoreCase("tp") -> {
                if (!enforceAdminPermission(player)) return false;

                if (args.length == 0) {
                    CommandInfo.sendUsage(player, "Teleport to a player.", "tp", "<player>");
                    return false;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    warn(player, "Unable to find the player §4" + args[0] + "§c.");
                    return false;
                }

                if (target.equals(player)) {
                    warn(player, "You cannot teleport to yourself.");
                    return false;
                }

                player.teleport(target.getLocation());
                inform(player, "Teleporting to §d" + target.getName() + "§7.");
                return true;
            }
            case Player player when label.equalsIgnoreCase("tphere") -> {
                if (!enforceAdminPermission(player)) return false;

                if (args.length == 0) {
                    CommandInfo.sendUsage(player, "Teleport a player to you.", "tphere", "<player>");
                    return false;
                }

                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    warn(player, "Unable to find the player §4" + args[0] + "§c.");
                    return false;
                }

                if (target.equals(player)) {
                    warn(player, "You cannot teleport yourself to yourself.");
                    return false;
                }

                target.teleport(player.getLocation());
                inform(player, "Teleported §d" + target.getName() + "§7 to you.");
                return true;
            }
            case Player player when label.equalsIgnoreCase("tpall") -> {
                if (!enforceAdminPermission(player)) return false;

                for (Player target : Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(player)).toList()) {
                    target.teleport(player.getLocation());
                }

                inform(player, "Teleporting all players to you...");
                return true;
            }
            case Player player when label.equalsIgnoreCase("tppos") -> {
                if (!enforceAdminPermission(player)) return false;
                if (args.length < 3) {
                    CommandInfo.sendUsage(player,
                            "Teleport a player to a specific position. World options: NETHER, OVERWORLD, THE_END",
                            "tppos", "<x>", "<y>", "<z>", "<player>", "[world]"
                    );
                    return false;
                }

                try {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);

                    Player target;
                    if (args.length >= 4) {
                        target = Bukkit.getPlayerExact(args[3]);
                        if (target == null) {
                            warn(player, "Target player not found.");
                            return false;
                        }
                    } else {
                        target = player;
                    }

                    World world;
                    if (args.length >= 5) {
                        switch (args[4].toUpperCase()) {
                            case "NETHER" -> world = Bukkit.getWorld("world_nether");
                            case "THE_END" -> world = Bukkit.getWorld("world_the_end");
                            case "OVERWORLD" -> world = Bukkit.getWorld("world");
                            default -> {
                                warn(player, "Invalid world. Use OVERWORLD, NETHER, or THE_END.");
                                return false;
                            }
                        }
                        if (world == null) {
                            warn(player, "World not found on the server.");
                            return false;
                        }
                    } else {
                        world = target.getWorld();
                    }

                    String displayWorldName;
                    switch (world.getEnvironment()) {
                        case NETHER -> displayWorldName = "NETHER";
                        case THE_END -> displayWorldName = "THE_END";
                        default -> displayWorldName = "OVERWORLD";
                    }

                    Location location = new Location(world, x, y, z);
                    target.teleport(location);

                    if (target.equals(player)) {
                        inform(player, "§7Teleported to position §d(" + x + ", " + y + ", " + z + ") §7in §f§l" + displayWorldName);
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.75f);
                    } else {
                        inform(player, "§7Teleported §d" + target.getName() + " §7to position §d(" + x + ", " + y + ", " + z + ") §7in §f§l" + displayWorldName);
                        inform(target, "§7Teleported to position §d(" + x + ", " + y + ", " + z + ") §7in §f§l" + displayWorldName);
                        player.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.75f);
                    }
                    return true;
                } catch (NumberFormatException e) {
                    warn(player, "Invalid coordinates. Please provide valid numbers.");
                    return false;
                }
            }
            default -> {
                warn(sender, "Only players can execute teleport commands.");
                return false;
            }
        }
    }
}
