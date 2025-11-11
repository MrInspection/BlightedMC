package fr.moussax.blightedMC.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class GodCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!label.equalsIgnoreCase("god") || !(sender instanceof Player player)) return false;
    if (!enforceAdminPermission(player)) return false;

    if (args.length == 0) {
      toggleGodMode(player);
      return true;
    }

    Player target = Bukkit.getPlayerExact(args[0]);
    if (target == null) {
      warn(player, "Unable to find the player §4" + args[0] + "§c.");
      return false;
    }
    toggleTargetGodMode(player, target);
    return true;
  }

  private void toggleGodMode(Player p) {
    if (p.isInvulnerable()) {
      p.setInvulnerable(false);
      inform(p, "You are no longer in §f§lGOD §7mode.");
    } else {
      p.setInvulnerable(true);
      inform(p, "You are now in §f§lGOD §7mode.");
    }
  }

  private void toggleTargetGodMode(Player sender, Player target) {
    boolean isInvulnerable = target.isInvulnerable();

    if (sender.equals(target)) {
      toggleGodMode(sender);
      return;
    }

    toggleGodMode(target);
    if (isInvulnerable) {
      inform(sender, "You disabled §f§lGOD §7mode for §d" + target.getName() + "§7.");
    } else {
      inform(sender, "You enabled §f§lGOD §7mode for §d" + target.getName() + "§7.");
    }
  }
}
