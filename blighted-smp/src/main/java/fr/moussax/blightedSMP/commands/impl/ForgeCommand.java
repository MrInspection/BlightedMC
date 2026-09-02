package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.commands.PlayerCommand;
import fr.moussax.blightedSMP.engine.items.recipes.forging.menu.ForgeMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class ForgeCommand extends PlayerCommand {
    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        BlightedSMP.menuManager().openMenu(new ForgeMenu(null), player);
        return true;
    }
}
