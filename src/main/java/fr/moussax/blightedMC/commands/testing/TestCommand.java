package fr.moussax.blightedMC.commands.testing;

import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.inform;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (!(label.equalsIgnoreCase("test") && sender instanceof Player player)) return false;

        if (args.length == 0) {
            inform(player, "@params: §dXP§7, §dBOSS_SPAWN");
            return false;
        }

        if (args[0].equalsIgnoreCase("XP")) {
            SoundSequence.XP_PICKUP.play(player.getLocation());
            inform(player, "Executing §dXP §7sound sequence test.");
            return true;
        }

        if (args[0].equalsIgnoreCase("BOSS_SPAWN")) {
            SoundSequence.BOSS_SPAWN.play(player.getLocation());
            inform(player, "Executing §dBOSS_SPAWN §7sound sequence test.");
            return true;
        }
        return false;
    }
}
