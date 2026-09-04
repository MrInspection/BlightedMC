package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.bedrock.commands.PlayerCommand;
import fr.moussax.blightedSMP.engine.entities.rituals.menu.RitualAltarMenu;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public final class AltarCommand extends PlayerCommand {
    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        new RitualAltarMenu(null).open(player);
        return true;
    }
}
