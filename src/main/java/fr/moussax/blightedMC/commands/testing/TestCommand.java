package fr.moussax.blightedMC.commands.testing;

import fr.moussax.blightedMC.utils.ItemBuilder;
import fr.moussax.blightedMC.utils.sound.SoundSequence;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

import static fr.moussax.blightedMC.utils.formatting.Formatter.inform;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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

        if (args[0].equalsIgnoreCase("b")) {
            PlayerInventory inv = player.getInventory();
            inv.setHelmet(new ItemBuilder(Material.BLACK_BANNER, "§dBlighted Banner")
                .addBannerPatterns(List.of(
                    new Pattern(DyeColor.PURPLE, PatternType.CURLY_BORDER),
                    new Pattern(DyeColor.BLACK, PatternType.BRICKS),
                    new Pattern(DyeColor.BLACK, PatternType.SMALL_STRIPES),
                    new Pattern(DyeColor.PURPLE, PatternType.GUSTER),
                    new Pattern(DyeColor.PURPLE, PatternType.CIRCLE),
                    new Pattern(DyeColor.BLACK, PatternType.FLOW))
                )
                .addLore("§7An eerie looking banner.")
                .toItemStack());
        }

        if (args[0].equalsIgnoreCase("BOSS_SPAWN")) {
            SoundSequence.BOSS_SPAWN.play(player.getLocation());
            inform(player, "Executing §dBOSS_SPAWN §7sound sequence test.");
            return true;
        }
        return false;
    }
}
