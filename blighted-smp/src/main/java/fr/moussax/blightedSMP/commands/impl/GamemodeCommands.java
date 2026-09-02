package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.commands.AdminCommand;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;

public final class GamemodeCommands extends AdminCommand {

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("gmc")) {
            return switchGamemode(player, GameMode.CREATIVE);
        }

        if (label.equalsIgnoreCase("gms")) {
            return switchGamemode(player, GameMode.SURVIVAL);
        }

        if (label.equalsIgnoreCase("gmspec")) {
            return switchGamemode(player, GameMode.SPECTATOR);
        }

        if (label.equalsIgnoreCase("gma")) {
            return switchGamemode(player, GameMode.ADVENTURE);
        }

        return true;
    }

    private boolean switchGamemode(Player player, GameMode gamemode) {
        player.setGameMode(gamemode);
        inform(player, " §eYou are now in §f" + gamemode.name().toUpperCase() + " §emode.");
        return true;
    }
}
