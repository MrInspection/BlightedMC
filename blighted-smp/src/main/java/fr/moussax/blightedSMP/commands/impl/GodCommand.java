package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.blightedSMP.commands.AdminCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.InteractiveMessage.text;

public final class GodCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        toggleGodMode(player);
        return true;
    }

    private void toggleGodMode(Player player) {
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
            player.sendMessage(" §fGod Mode §etoggled §cOFF§e.");
        } else {
            player.setInvulnerable(true);
            text(" §fGod Mode §etoggled §aON§e. ")
                    .hoverAndExecute("§4[§c➟ Disable§4]", "§eClick to disable your §6God Mode§e.", "/god")
                    .send(player);
        }
    }
}
