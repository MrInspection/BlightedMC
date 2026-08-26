package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import fr.moussax.blightedMC.shared.ui.toast.AdvancementToast;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class TestCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        AdvancementToast.task(player, "Command looped 50x for 2 ticks", Material.REPEATING_COMMAND_BLOCK);
        AdvancementToast.challenge(player, "Call the Corrupted Champion", Material.SCULK_SHRIEKER);
        return true;
    }
}
