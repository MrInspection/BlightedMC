package fr.moussax.blightedMC.commands.testing;

import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.MessageUtils.informSender;

public class TestCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!(label.equalsIgnoreCase("test") && sender instanceof Player player)) return false;

    // Test
    SoundSequence.BOSS_SPAWN.play(player.getLocation());
    informSender(player, "Executing §dBOSS_SPAWN §7sound sequence test.");
    return true;
  }
}
