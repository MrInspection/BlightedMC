package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.entities.rituals.menu.RitualAltarMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class AltarCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(label.equalsIgnoreCase("altar") && sender instanceof Player player)) return false;
        BlightedMC.menuManager().openMenu(new RitualAltarMenu(null), player);
        return true;
    }
}
