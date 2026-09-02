package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.commands.AdminCommand;
import fr.moussax.blightedSMP.commands.utils.CommandFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static fr.moussax.bedrock.text.InteractiveMessage.text;
import static fr.moussax.bedrock.text.Messenger.warn;

public final class LoopCommand extends AdminCommand {
    private static final int MIN_AMOUNT = 2;
    private static final int MAX_AMOUNT = 50;
    private static final int MIN_DELAY_TICKS = 1;
    private static final int MAX_DELAY_TICKS = 180;

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (args.length < 3) {
            CommandFormatter.sendUsage(player, "loop [2-50] [1-180] <command>", "Bulk execute a command.");
            return false;
        }

        int amount;
        int delay;

        try {
            amount = Integer.parseInt(args[0]);
            delay = Integer.parseInt(args[1]);
        } catch (NumberFormatException _) {
            warn(player, "Amount and delay must be numbers.");
            return false;
        }

        if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
            warn(player, "Amount must be between §d" + MIN_AMOUNT + "§c and §d" + MAX_AMOUNT + "§c.");
            return false;
        }

        if (delay < MIN_DELAY_TICKS || delay > MAX_DELAY_TICKS) {
            warn(player, "Delay must be between §d" + MIN_DELAY_TICKS + "§c and §d" + MAX_DELAY_TICKS + "§c ticks.");
            return false;
        }

        String commandToExecute = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (commandToExecute.startsWith("loop ")) {
            warn(player, "You cannot loop the §4loop §ccommand.");
            return false;
        }

        for (int i = 0; i < amount; i++) {
            int ticksDelay = i * delay;
            Bukkit.getScheduler().runTaskLater(BlightedSMP.getInstance(), () -> Bukkit.dispatchCommand(player, commandToExecute), ticksDelay);
        }

        text("\n§e Looping §7x" + amount + " §eyour ")
                .hoverAndSuggest("§fCOMMAND", "§eClick to fill §dcommand §ein chat", "/" + commandToExecute)
                .append(" §ewith §d" + delay + " tick§e delay...\n")
                .send(player);

        return true;
    }
}
