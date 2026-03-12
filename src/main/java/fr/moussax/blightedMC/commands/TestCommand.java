package fr.moussax.blightedMC.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, String label, String @NonNull [] args) {
        if (!(label.equalsIgnoreCase("test") && sender instanceof Player player)) return false;

        // TODO

        return true;
    }
}
