package fr.moussax.blightedMC.commands.testing;

import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.inform;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, String label, String @NonNull [] args) {
        if (!(label.equalsIgnoreCase("test") && sender instanceof Player player)) return false;

        if (args.length == 0) {
            inform(player, "@params: §dXP, SLAYER_BOSS, BOSS, BOSS_DEFEAT, GEMSTONE");
            return false;
        }

        if (args[0].equalsIgnoreCase("GEMSTONE")) {
            SoundSequence.BLIGHTED_GEMSTONE_CONSUME.play(player.getLocation());

            inform(player, "Executing §dGEMSTONE §7sound sequence test.");
            player.getInventory().addItem(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(player.getUniqueId()).toItemStack());
            return true;
        }

        if (args[0].equalsIgnoreCase("XP")) {
            SoundSequence.XP_PICKUP.play(player.getLocation());

            inform(player, "Executing §dXP §7sound sequence test.");
            return true;
        }

        if (args[0].equalsIgnoreCase("SLAYER_BOSS")) {
            SoundSequence.SLAYER_MOB_SPAWN.play(player.getLocation());
            inform(player, "Executing §dSLAYER_BOSS_SPAWN §7sound sequence test.");
            return true;
        }

        if (args[0].equalsIgnoreCase("BOSS")) {
            SoundSequence.ANCIENT_MOB_SPAWN.play(player.getLocation());
            inform(player, "Executing §dBLIGHTED_BOSS §7sound sequence test.");
            return true;
        }

        if (args[0].equalsIgnoreCase("BOSS_DEFEAT")) {
            SoundSequence.ANCIENT_MOB_DEFEAT.play(player.getLocation());
            inform(player, "Executing §dBLIGHTED_DEFEAT §7sound sequence test.");
            return true;
        }
        return false;
    }
}
