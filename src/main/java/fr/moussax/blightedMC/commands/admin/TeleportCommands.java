package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.utils.formatting.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.MessageUtils.*;

public class TeleportCommands implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if(label.equalsIgnoreCase("tp") && sender instanceof Player player) {
      enforceAdminPermission(player);

      if(args.length == 0) {
        MessageUtils.informSender(sender,
          "",
          "§8 ■ §7Usage: §6/§rtp §6<§fplayer§6>",
          "§8 ■ §7Description: §eTeleport to a player.",
          ""
        );
        return false;
      }

      Player target = Bukkit.getPlayerExact(args[0]);
      if(target == null) {
        warnSender(player, "Unable to find the player §4" + args[0] + "§c.");
        return false;
      }

      if(target.equals(player)) {
        warnSender(player, "You cannot teleport to yourself.");
        return false;
      }

      player.teleport(target.getLocation());
      informSender(player, "Teleporting to §d" + target.getName() + "§7.");
      return true;
    }

    if(label.equalsIgnoreCase("tphere") && sender instanceof Player player) {
      enforceAdminPermission(player);

      if(args.length == 0) {
        MessageUtils.informSender(sender,
          "",
          "§8 ■ §7Usage: §6/§rtphere §6<§fplayer§6>",
          "§8 ■ §7Description: §eTeleport a player to you.",
          ""
        );
        return false;
      }

      Player target = Bukkit.getPlayerExact(args[0]);
      if(target == null) {
        warnSender(player, "Unable to find the player §4" + args[0] + "§c.");
        return false;
      }

      if(target.equals(player)) {
        warnSender(player, "You cannot teleport yourself to yourself.");
        return false;
      }

      target.teleport(player.getLocation());
      informSender(player, "Teleported §d" + target.getName() + "§7 to you.");
      return true;
    }

    if(label.equalsIgnoreCase("tpall") && sender instanceof Player player) {
      enforceAdminPermission(player);

      for(Player target : Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(player)).toList() ) {
        target.teleport(player.getLocation());
      }

      informSender(player, "Teleporting all players to you...");
      return true;
    }

    if (label.equalsIgnoreCase("tppos") && sender instanceof Player player) {
      enforceAdminPermission(player);
      if (args.length < 3) {
        MessageUtils.informSender(sender,
          "",
          "§8 ■ §7Usage: §6/§rtppos §6<§fx§6> <§fy§6> <§fz§6> <§fplayer§6> [§fworld§6]",
          "§8 ■ §7Description: §eTeleport a player to a specific position.",
          "§8 ■ §7Params: §fWorld §6➡ §fNETHER, OVERWORLD, THE_END",
          ""
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
            warnSender(player, "Target player not found.");
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
              warnSender(player, "Invalid world. Use OVERWORLD, NETHER, or THE_END.");
              return false;
            }
          }
          if (world == null) {
            warnSender(player, "World not found on the server.");
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
          informSender(player, "§7Teleported to position §d(" + x + ", " + y + ", " + z + ") §7in §f§l" + displayWorldName);
          player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.75f);
        } else {
          informSender(player, "§7Teleported §d" + target.getName() + " §7to position §d(" + x + ", " + y + ", " + z + ") §7in §f§l" + displayWorldName);
          informSender(target, "§7Teleported to position §d(" + x + ", " + y + ", " + z + ") §7in §f§l" + displayWorldName);
          player.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.75f);
        }
        return true;
      } catch (NumberFormatException e) {
        warnSender(player, "Invalid coordinates. Please provide valid numbers.");
        return false;
      }
    }

    return false;
  }
}
