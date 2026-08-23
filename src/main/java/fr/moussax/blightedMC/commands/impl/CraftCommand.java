package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.commands.PlayerCommand;
import fr.moussax.blightedMC.engine.items.recipes.crafting.menu.CraftingTableMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class CraftCommand extends PlayerCommand {
    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        BlightedMC.menuManager().openMenu(new CraftingTableMenu(), player);
        return true;
    }
}
