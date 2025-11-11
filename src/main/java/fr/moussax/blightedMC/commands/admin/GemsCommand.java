package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.utils.commands.CommandArgument;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.formatting.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

@CommandArgument(suggestions = {"add", "remove", "set", "reset", "resetall", "giveall", "help"})
@CommandArgument(position = 1, after = {"add", "remove", "set", "reset"}, suggestions = {"$players"})
public class GemsCommand implements CommandExecutor {

  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!label.equalsIgnoreCase("gems") || !(sender instanceof Player player)) return false;
    if (!enforceAdminPermission(player)) return false;

    if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
      CommandInfo.sendCommands(player, "COMMANDS", "Gems Currency",
        CommandInfo.Entry.of("Give gems to a player.", "gems", "add", "<player>", "[amount]"),
        CommandInfo.Entry.of("Take gems from a player.", "gems", "remove", "<player>", "[amount]"),
        CommandInfo.Entry.of("Set gems for a player.", "gems", "set", "<player>", "[amount]"),
        CommandInfo.Entry.of("Reset gems for a player.", "gems", "reset", "<player>"),
        CommandInfo.Entry.of("Give gems to everyone.", "gems", "giveall", "[amount]"),
        CommandInfo.Entry.of("Reset everyone’s balance.", "gems", "resetall"),
        CommandInfo.Entry.of("Prints this help message.", "gems", "help")
      );
      return false;
    }

    switch (args[0].toLowerCase()) {
      case "add" -> {
        return handleModify(player, args, true);
      }
      case "remove" -> {
        return handleModify(player, args, false);
      }
      case "set" -> {
        return handleSet(player, args);
      }
      case "reset" -> {
        return handleReset(player, args);
      }
      case "resetall" -> {
        return handleResetAll(player);
      }
      case "giveall" -> {
        return handleGiveAll(player, args);
      }
      default -> {
        warn(player, "Unknown §4gems §csubcommand.");
        return false;
      }
    }
  }

  private boolean handleModify(Player sender, String[] args, boolean add) {
    if (args.length < 3) {
      CommandInfo.sendUsage(sender,
        (add ? "Add" : "Remove") + " gems to a player.",
        "gems", (add ? "add" : "remove"), "<player>", "[amount]"
      );
      return false;
    }

    Player target = Bukkit.getPlayerExact(args[1]);
    if (target == null) {
      warn(sender, "Unable to find the player §4" + args[1] + "§c.");
      return false;
    }

    int amount;
    try {
      amount = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      warn(sender, "Amount must be a positive number.");
      return false;
    }

    BlightedPlayer bPlayer = BlightedPlayer.getBlightedPlayer(target);

    if (add) {
      bPlayer.addGems(amount);
      sender.sendMessage("§8 ■ §7You gave §d" + amount + " §7gems to §d" + target.getName() + "§7.");
      target.sendMessage("§8 ■ §7You received §d" + amount + " §7gems.");
      return true;
    }

    bPlayer.removeGems(amount);
    sender.sendMessage("§8 ■ §7You removed §d" + amount + " §7gems from §d" + target.getName() + "§7.");
    target.sendMessage("§8 ■ §7You lost §d" + amount + " §7gems.");

    return true;
  }

  private boolean handleSet(Player sender, String[] args) {
    if (args.length < 3) {
      CommandInfo.sendUsage(sender, "Set gems for a player.",
        "gems", "set", "<player>", "[amount]"
      );
      return false;
    }

    Player target = Bukkit.getPlayerExact(args[1]);
    if (target == null) {
      warn(sender, "Unable to find the player §4" + args[1] + "§c.");
      return false;
    }

    int amount;
    try {
      amount = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      warn(sender, "Amount must be a positive number.");
      return false;
    }

    BlightedPlayer.getBlightedPlayer(target).setGems(amount);
    sender.sendMessage("§8 ■ §7You have set §d" + target.getName() + "§7's gems balance to §d" + amount + "§7.");
    target.sendMessage("§8 ■ §7You gems has been set to §d" + amount + "§7.");
    return true;
  }

  private boolean handleReset(Player sender, String[] args) {
    if (args.length < 2) {
      CommandInfo.sendUsage(sender, "Reset gems for a player.",
        "gems", "reset", "<player>"
      );
      return false;
    }

    Player target = Bukkit.getPlayerExact(args[1]);
    if (target == null) {
      warn(sender, "Unable to find the player §4" + args[1] + "§c.");
      return false;
    }

    BlightedPlayer.getBlightedPlayer(target).setGems(0);
    sender.sendMessage("§8 ■ §7You have reset §d" + target.getName() + "§7's gems.");
    return true;
  }

  private boolean handleResetAll(Player sender) {
    Bukkit.getOnlinePlayers().forEach(player -> {
      BlightedPlayer bPlayer = BlightedPlayer.getBlightedPlayer(player);
      bPlayer.setGems(0);
    });
    sender.sendMessage("§8 ■ §7All online players favor have been reset to §d0§7.");
    return true;
  }

  private boolean handleGiveAll(Player sender, String[] args) {
    if (args.length < 2) {

      CommandInfo.sendUsage(sender, "Give all online players gems.", "gems", "giveall", "[amount]");
      return false;
    }

    int amount;
    try {
      amount = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      warn(sender, "Amount must be a positive number.");
      return false;
    }

    Bukkit.getOnlinePlayers().forEach(player -> {
      BlightedPlayer bPlayer = BlightedPlayer.getBlightedPlayer(player);
      bPlayer.addGems(amount);
      player.sendMessage("§8 ■ §7You received §d" + amount + " §7gems.");
    });

    inform(sender, "§8 ■ §7You gave §d" + amount + " §7gems to all §donline §7players.");
    return true;
  }
}
