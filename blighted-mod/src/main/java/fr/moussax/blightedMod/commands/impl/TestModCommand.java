package fr.moussax.blightedMod.commands.impl;

import fr.moussax.blightedMod.commands.ModerationCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;

public final class TestModCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        String category = arguments.length > 0 ? arguments[0].toLowerCase() : "all";

        inform(moderator, " ");
        inform(moderator, " ");

        if (category.equals("all") || category.equals("target") || category.equals("user") || category.equals("player")) {
            inform(moderator, "§8--- Target / Player Facing Messages ---");
            inform(moderator, " §c⌚ §cYou are muted for §d1h §cfor §bAdvertising Social Media§c.");
            inform(moderator, " §c⌚ §cYou are muted for §cfor §bAdvertising Social Media§c.");
            inform(moderator, " §a⚑ §7You are no longer muted.");
            inform(moderator, " ");
            inform(moderator, "§b ❄ §b§lYOU HAVE BEEN FROZEN BY A MODERATOR!");
            inform(moderator, "§c ⚠ Do not log out or you will be permanently banned.");
            inform(moderator, " ");
            inform(moderator, "§b ❄ §7You have been unfrozen, you can move again.");
            inform(moderator, " §c⌚ §cPlease wait §d5s §cbefore chatting again.");
        }

        inform(moderator, " ");
        inform(moderator, " ");
        return true;
    }
}
