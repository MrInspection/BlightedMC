package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.commands.CommandArgument;
import fr.moussax.blightedMod.commands.ModerationCommand;
import fr.moussax.blightedMod.moderator.ModerationManager;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;
import static fr.moussax.bedrock.text.Messenger.warn;

/**
 * Command to switch active chat channel (/chat staff, /chat all).
 */
@CommandArgument(position = 0, suggestions = {"$channels"})
public final class ChatCommand extends ModerationCommand {
    private static final String PREFIX = " §9§lMOD §f| §7";

    @Override
    protected boolean executeModeration(Player player, Command command, String label, String[] arguments) {
        if (arguments.length == 0) {
            ModerationManager.ChatChannel currentChannel = getModerationManager().getChatChannel(player);
            inform(player, PREFIX + "Currently in active chat channel: §e" + currentChannel.name());
            warn(player, "Usage: /chat <staff|all>");
            return true;
        }

        String targetChannel = arguments[0].toLowerCase();
        if (targetChannel.equals("staff") || targetChannel.equals("s")) {
            if (!getModerationManager().isModerator(player)) {
                warn(player, "Only staff members can access the staff chat channel.");
                return false;
            }

            getModerationManager().setChatChannel(player, ModerationManager.ChatChannel.STAFF);
            inform(player, PREFIX + "Switched active chat channel to §9STAFF§7.");
            return true;
        }

        if (targetChannel.equals("all") || targetChannel.equals("a") || targetChannel.equals("global") || targetChannel.equals("normal")) {
            getModerationManager().setChatChannel(player, ModerationManager.ChatChannel.ALL);
            inform(player, PREFIX + "Switched active chat channel to §bALL§7.");
            return true;
        }

        warn(player, "Unknown chat channel. Usage: /chat <staff|all>");
        return false;
    }
}
