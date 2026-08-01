package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import fr.moussax.blightedMC.commands.CommandFormatter;
import fr.moussax.blightedMC.commands.utils.CommandArgument;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.blightedMC.utils.Formatter.inform;
import static fr.moussax.blightedMC.utils.Formatter.warn;

@CommandArgument(position = 0, suggestions = {"add", "remove", "set", "reset", "resetall", "giveall", "help"})
@CommandArgument(position = 1, after = {"add", "remove", "set", "reset"}, suggestions = {"$players"})
public final class GemsCommand extends AdminCommand {

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            CommandFormatter.sendCommands(player, "COMMANDS", "Gems Currency",
                    CommandFormatter.Entry.of("Give gems to a player.", "gems", "add", "<player>", "[amount]"),
                    CommandFormatter.Entry.of("Take gems from a player.", "gems", "remove", "<player>", "[amount]"),
                    CommandFormatter.Entry.of("Set gems for a player.", "gems", "set", "<player>", "[amount]"),
                    CommandFormatter.Entry.of("Reset gems for a player.", "gems", "reset", "<player>"),
                    CommandFormatter.Entry.of("Give gems to everyone.", "gems", "giveall", "[amount]"),
                    CommandFormatter.Entry.of("Reset everyone's balance.", "gems", "resetall"),
                    CommandFormatter.Entry.of("Prints this help message.", "gems", "help")
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
            CommandFormatter.sendUsage(
                    sender,
                    (add ? "Add" : "Remove") + " gems to a player.",
                    "gems",
                    add ? "add" : "remove",
                    "<player>",
                    "[amount]"
            );
            return false;
        }

        Player target = requireTarget(sender, args[1]);
        if (target == null) return false;

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 0) {
                warn(sender, "Amount must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            warn(sender, "Amount must be a positive number.");
            return false;
        }

        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(target);

        if (add) {
            blightedPlayer.addGems(amount);
            inform(sender, " §eGave §d" + amount + " gems to §5" + target.getName() + "§e.");
            inform(target, " §7You received §d" + amount + " §7gems.");
            return true;
        }

        blightedPlayer.removeGems(amount);
        inform(sender, " §eRemoved §d" + amount + " §egems from §5" + target.getName() + "§e.");
        inform(target, " §7You lost §d" + amount + " §7gems from your balance.");
        return true;
    }

    private boolean handleSet(Player sender, String[] args) {
        if (args.length < 3) {
            CommandFormatter.sendUsage(
                    sender,
                    "Set gems for a player.",
                    "gems",
                    "set",
                    "<player>",
                    "[amount]"
            );
            return false;
        }

        Player target = requireTarget(sender, args[1]);
        if (target == null) {
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 0) {
                warn(sender, "Amount must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            warn(sender, "Amount must be a positive number.");
            return false;
        }

        BlightedPlayer.getBlightedPlayer(target).setGems(amount);
        inform(sender, "§e Set §d" + target.getName() + "§e's gems balance to §d" + amount + "§e.");
        inform(target, "§7 Your gems balance has been set to §d" + amount + "§7.");
        return true;
    }

    private boolean handleReset(Player sender, String[] args) {
        if (args.length < 2) {
            CommandFormatter.sendUsage(
                    sender,
                    "Reset gems for a player.",
                    "gems",
                    "reset",
                    "<player>"
            );
            return false;
        }

        Player target = requireTarget(sender, args[1]);
        if (target == null) return false;

        BlightedPlayer.getBlightedPlayer(target).setGems(0);
        inform(sender, "§e You reset §d" + target.getName() + "§e's gems.");
        inform(target, " §7Your gems balance has been reset.");
        return true;
    }

    private boolean handleResetAll(Player sender) {
        Bukkit.getOnlinePlayers().forEach(player ->
                BlightedPlayer.getBlightedPlayer(player).setGems(0)
        );

        inform(sender, "Reset all §donline §7players' gems.");
        return true;
    }

    private boolean handleGiveAll(Player sender, String[] args) {
        if (args.length < 2) {
            CommandFormatter.sendUsage(
                    sender,
                    "Give all online players gems.",
                    "gems",
                    "giveall",
                    "[amount]"
            );
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount < 0) {
                warn(sender, "Amount must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            warn(sender, "Amount must be a positive number.");
            return false;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            BlightedPlayer.getBlightedPlayer(player).addGems(amount);
            inform(player, " §7You received §d" + amount + " §7gems.");
        });

        inform(sender, " §eYou gave all players §d" + amount + " §egems.");
        return true;
    }
}
