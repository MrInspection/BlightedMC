package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.BlightedSMP;
import fr.moussax.blightedSMP.commands.PlayerCommand;
import fr.moussax.blightedSMP.engine.items.recipes.crafting.menu.CraftingTableMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class CraftCommand extends PlayerCommand {
    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        BlightedSMP.menuManager().openMenu(new CraftingTableMenu(), player);
        return true;
    }
}
