package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.utils.formatting.CommandInfo;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

public class LoopCommand implements CommandExecutor {
    private static final int MIN_AMOUNT = 2;
    private static final int MAX_AMOUNT = 50;
    private static final int MIN_DELAY_TICKS = 1;
    private static final int MAX_DELAY_TICKS = 180;

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (!label.equalsIgnoreCase("loop") || !(sender instanceof Player player)) return false;
        if (!enforceAdminPermission(player)) return false;

        if (args.length < 3) {
            CommandInfo.sendUsage(player, "Bulk execute a command",
                    "loop", "[" + MIN_AMOUNT + "-" + MAX_AMOUNT + "]", "[" + MIN_DELAY_TICKS + "-" + MAX_DELAY_TICKS + "]", "<command>");
            return false;
        }

        int amount;
        int delay;

        try {
            amount = Integer.parseInt(args[0]);
            delay = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            warn(sender, "Amount and delay must be numbers.");
            return false;
        }

        if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
            warn(sender, "Amount must be between §d" + MIN_AMOUNT + "§c and §d" + MAX_AMOUNT + "§c.");
            return false;
        }

        if (delay < MIN_DELAY_TICKS || delay > MAX_DELAY_TICKS) {
            warn(sender, "Delay must be between §d" + MIN_DELAY_TICKS + "§c and §d" + MAX_DELAY_TICKS + "§c ticks.");
            return false;
        }

        String commandToExecute = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (commandToExecute.startsWith("loop ")) {
            warn(sender, "You cannot loop the §4loop §ccommand.");
            return false;
        }

        for (int i = 0; i < amount; i++) {
            int ticksDelay = i * delay;
            Bukkit.getScheduler().runTaskLater(BlightedMC.getInstance(), () -> Bukkit.dispatchCommand(player, commandToExecute), ticksDelay);
        }

        TextComponent message = new TextComponent("\n§8 ■ §7Looping your §f");
        TextComponent commandWord = createInteractiveText(
                "§f§lCOMMAND",
                "§7Click to fill §dcommand §7in chat",
                ClickEvent.Action.SUGGEST_COMMAND,
                "/" + commandToExecute
        );
        TextComponent afterCommand = new TextComponent(" §7with §d" + delay + " tick§7 delay. §d(Repeat " + amount + "x)\n");

        message.addExtra(commandWord);
        message.addExtra(afterCommand);

        player.spigot().sendMessage(message);
        return true;
    }
}
