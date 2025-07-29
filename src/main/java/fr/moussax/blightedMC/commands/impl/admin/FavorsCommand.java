package fr.moussax.blightedMC.commands.impl.admin;

import fr.moussax.blightedMC.commands.CommandArgument;
import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

@CommandArgument(position = 0, suggestions = {"add","remove", "set", "reset", "resetall", "giveall", "help"})
@CommandArgument(position = 1, after = {"add","remove", "set", "reset"}, suggestions = {"$players"})
public class FavorsCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if(label.equalsIgnoreCase("favors") && sender instanceof Player player) {

      if(!player.isOp()) {
        player.sendMessage("§4 ■ §cYou must be an admin to use this command.");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
        return false;
      }

      if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
        player.sendMessage(" ");
        player.sendMessage("§8 ■ §6/§ffavors §6add §6<§fplayer§6> §6[§famount§6] §8- §7Give favors to a player.");
        player.sendMessage("§8 ■ §6/§ffavors §6remove §6<§fplayer§6> §6[§famount§6] §8- §7Take favors from a player.");
        player.sendMessage("§8 ■ §6/§ffavors §6set §6<§fplayer§6> §6[§famount§6] §8- §7Set favors for a player.");
        player.sendMessage("§8 ■ §6/§ffavors §6reset §6<§fplayer§6> §8- §7Reset favors for a player.");
        player.sendMessage("§8 ■ §6/§ffavors §6giveall §6[§famount§6] §8- §7Favors for everyone.");
        player.sendMessage("§8 ■ §6/§ffavors §6resetall §8- §7Reset §eeveryone§7's balance.");
        player.sendMessage("§8 ■ §6/§ffavors §6help §8- §7Prints this help message.");
        player.sendMessage(" ");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 100f, 2f);
        return false;
      }

      switch (args[0].toLowerCase()) {
        case "add" -> handleModify(player, args, true);
        case "remove" -> handleModify(player, args, false);
        case "set" -> handleSet(player, args);
        case "reset" -> handleReset(player, args);
        case "resetall" -> handleResetAll(player);
        case "giveall" -> handleGiveAll(player, args);
        default -> {
          player.sendMessage("§4 ■ §cUnknown subcommand.");
          player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
          return false;
        }
      }
    }
    return false;
  }

  private boolean handleModify(Player sender, String[] args, boolean add) {
    if(args.length < 3) {
      sender.sendMessage("§8 ■ §7Usage: §6/§ffavors §6" + (add ? "add" : "remove") + " §6<§fplayer§6> §6[§famount§6]");
      return false;
    }

    Player target = Bukkit.getPlayerExact(args[1]);
    if(target == null) {
      sender.sendMessage("§4 ■ §cUnable to find the desired player.");
      sender.playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
      return false;
    }

    int amount;
    try {
      amount = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      sender.sendMessage("§4 ■ §cAmount must be a positive number.");
      sender.playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
      return false;
    }

    BlightedPlayer bPlayer = BlightedPlayer.getBlightedPlayer(target);

    if(add) {
      bPlayer.addFavors(amount);
      target.sendMessage("§8 ■ §7You received §d" + amount + " §7favors.");
      sender.sendMessage("§8 ■ §7You gave §d" + amount + " §7favors to §d" + target.getName() + "§7.");
      return true;
    }

    bPlayer.removeFavors(amount);
    target.sendMessage("§8 ■ §7You lost §d" + amount + " §7favors.");
    sender.sendMessage("§8 ■ §7You took §d" + amount + " §7favors from §d" + target.getName() + "§7.");

    return true;
  }

  private boolean handleSet(Player sender, String[] args) {
    if(args.length < 3) {
      sender.sendMessage("§8 ■ §7Usage: §6/§ffavors §6set §6<§fplayer§6> §6[§famount§6]");
      return false;
    }

    Player target = Bukkit.getPlayerExact(args[1]);
    if(target == null) {
      sender.sendMessage("§4 ■ §cUnable to find the desired player.");
      sender.playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
      return false;
    }

    int amount;
    try {
      amount = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      sender.sendMessage("§4 ■ §cAmount must be a positive number.");
      sender.playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
      return false;
    }

    BlightedPlayer.getBlightedPlayer(target).setFavors(amount);
    target.sendMessage("§8 ■ §7You favors has been set to §d" + amount + "§7.");

    sender.sendMessage("§8 ■ §7You have set §d" + target.getName() + "§7's favors balance to §d" + amount + "§7.");
    return true;
  }

  private boolean handleReset(Player sender, String[] args) {
    if(args.length < 2) {
      sender.sendMessage("§8 ■ §7Usage: §6/§ffavors §6reset §6<§fplayer§6>");
      return false;
    }

    Player target = Bukkit.getPlayerExact(args[1]);
    if(target == null) {
      sender.sendMessage("§4 ■ §cUnable to find the desired player.");
      sender.playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
      return false;
    }

    BlightedPlayer.getBlightedPlayer(target).setFavors(0);
    sender.sendMessage("§8 ■ §7You have reset §d" + target.getName() + "§7's favors.");
    return true;
  }

  private boolean handleResetAll(Player sender) {
    Bukkit.getOnlinePlayers().forEach(player -> {
      BlightedPlayer bPlayer = BlightedPlayer.getBlightedPlayer(player);
      bPlayer.setFavors(0);
    });
    sender.sendMessage("§8 ■ §7All online players favor have been reset to §d0§7.");
    return true;
  }

  private boolean handleGiveAll(Player sender, String[] args){
    if(args.length < 2) {
      sender.sendMessage("§8 ■ §7Usage: §6/§ffavors §6giveall §6[§famount§6]");
      return false;
    }

    int amount;
    try {
      amount = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      sender.sendMessage("§4 ■ §cAmount must be a positive number.");
      sender.playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
      return false;
    }

    Bukkit.getOnlinePlayers().forEach(player -> {
      BlightedPlayer bPlayer = BlightedPlayer.getBlightedPlayer(player);
      bPlayer.addFavors(amount);
      player.sendMessage("§8 ■ §7You received §d" + amount + " §7favors.");
    });

    sender.sendMessage("§8 ■ §7You gave §d" + amount + " §7favors to all §donline §7players.");
    return true;
  }
}
