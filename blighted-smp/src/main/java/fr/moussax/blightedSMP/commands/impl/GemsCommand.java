package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.commands.AdminCommand;
import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedSMP.engine.player.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

@CommandArgument(position = 0, suggestions = {"add", "remove", "set", "reset", "resetall", "giveall", "help"})
@CommandArgument(position = 1, path = {"add"}, suggestions = {"$players"})
@CommandArgument(position = 1, path = {"remove"}, suggestions = {"$players"})
@CommandArgument(position = 1, path = {"set"}, suggestions = {"$players"})
@CommandArgument(position = 1, path = {"reset"}, suggestions = {"$players"})
public final class GemsCommand extends AdminCommand {

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMenu(player);
            return false;
        }

        return switch (args[0].toLowerCase()) {
            case "add" -> handleModify(player, args, true);
            case "remove" -> handleModify(player, args, false);
            case "set" -> handleSet(player, args);
            case "reset" -> handleReset(player, args);
            case "resetall" -> handleResetAll(player);
            case "giveall" -> handleGiveAll(player, args);
            default -> {
                warn(player, "Unknown §4gems §csubcommand.");
                yield false;
            }
        };
    }

    private void sendHelpMenu(Player player) {
        player.sendMessage(" ");
        player.sendMessage(" ");
        player.sendMessage("    §e§lGEMS CURRENCY§f | §7Subcommands");
        player.sendMessage(" ");
        player.sendMessage("§f  • " + formatSyntax("gems add <player> [amount]") + " §f§l» §7Give gems to a player.");
        player.sendMessage("§f  • " + formatSyntax("gems remove <player> [amount]") + " §f§l» §7Take gems from a player.");
        player.sendMessage("§f  • " + formatSyntax("gems set <player> [amount]") + " §f§l» §7Set gems for a player.");
        player.sendMessage("§f  • " + formatSyntax("gems reset <player>") + " §f§l» §7Reset gems for a player.");
        player.sendMessage("§f  • " + formatSyntax("gems giveall [amount]") + " §f§l» §7Give gems to everyone.");
        player.sendMessage("§f  • " + formatSyntax("gems resetall") + " §f§l» §7Reset everyone's balance.");
        player.sendMessage("§f  • " + formatSyntax("gems help") + " §f§l» §7Prints this help message.");
        player.sendMessage(" ");
    }

    private String formatSyntax(String syntax) {
        StringBuilder builder = new StringBuilder("§e/");
        String[] arguments = syntax.strip().split("\\s+");

        for (int index = 0; index < arguments.length; index++) {
            if (index > 0) {
                builder.append(' ');
            }
            builder.append(formatToken(arguments[index]));
        }
        return builder.toString();
    }

    private String formatToken(String token) {
        if (token.length() > 1 && token.charAt(0) == '<' && token.charAt(token.length() - 1) == '>') {
            return "§e<§f" + token.substring(1, token.length() - 1) + "§e>";
        }
        if (token.length() > 1 && token.charAt(0) == '[' && token.charAt(token.length() - 1) == ']') {
            return "§e[§f" + token.substring(1, token.length() - 1) + "§e]";
        }
        return "§f" + token;
    }

    private boolean handleModify(Player sender, String[] args, boolean add) {
        if (args.length < 3) {
            warn(sender, "Usage: /gems " + (add ? "add" : "remove") + " <player> [amount]");
            return false;
        }

        Player target = requireTarget(sender, args[1]);
        if (target == null) return false;

        Integer amount = parseAmount(sender, args[2]);
        if (amount == null) return false;

        BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(target);

        if (add) {
            blightedPlayer.addGems(amount);
            inform(sender, " §eGave §d" + amount + " gems to §5" + target.getName() + "§e.");
            inform(target, " §7You received §d" + amount + " §7gems.");
        } else {
            blightedPlayer.removeGems(amount);
            inform(sender, " §eRemoved §d" + amount + " §egems from §5" + target.getName() + "§e.");
            inform(target, " §7You lost §d" + amount + " §7gems from your balance.");
        }
        return true;
    }

    private boolean handleSet(Player sender, String[] args) {
        if (args.length < 3) {
            warn(sender, "Usage: /gems set <player> [amount]");
            return false;
        }

        Player target = requireTarget(sender, args[1]);
        if (target == null) return false;

        Integer amount = parseAmount(sender, args[2]);
        if (amount == null) return false;

        BlightedPlayer.getBlightedPlayer(target).setGems(amount);
        inform(sender, "§e Set §d" + target.getName() + "§e's gems balance to §d" + amount + "§e.");
        inform(target, "§7 Your gems balance has been set to §d" + amount + "§7.");
        return true;
    }

    private boolean handleReset(Player sender, String[] args) {
        if (args.length < 2) {
            warn(sender, "Usage: /gems reset <player>");
            return false;
        }

        Player target = requireTarget(sender, args[1]);
        if (target == null) {
            return false;
        }

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
            warn(sender, "Usage: /gems giveall [amount]");
            return false;
        }

        Integer amount = parseAmount(sender, args[1]);
        if (amount == null) return false;

        Bukkit.getOnlinePlayers().forEach(player -> {
            BlightedPlayer.getBlightedPlayer(player).addGems(amount);
            inform(player, " §7You received §d" + amount + " §7gems.");
        });

        inform(sender, " §eYou gave all players §d" + amount + " §egems.");
        return true;
    }

    private Integer parseAmount(Player sender, String value) {
        try {
            int amount = Integer.parseInt(value);
            if (amount < 0) {
                warn(sender, "Amount must be a positive number.");
                return null;
            }
            return amount;
        } catch (NumberFormatException exception) {
            warn(sender, "Amount must be a positive number.");
            return null;
        }
    }
}
