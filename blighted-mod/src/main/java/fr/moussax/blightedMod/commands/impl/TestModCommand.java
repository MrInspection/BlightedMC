package fr.moussax.blightedMod.commands.impl;

import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.commands.ModerationCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import static fr.moussax.bedrock.text.Messenger.inform;

public final class TestModCommand extends ModerationCommand {

    @Override
    protected boolean executeModeration(Player moderator, Command command, String label, String[] arguments) {
        String category = arguments.length > 0 ? arguments[0].toLowerCase() : "all";

        inform(moderator, "§e=== [BlightedMod Message Formatting Test] ===");

        if (category.equals("all") || category.equals("mode")) {
            inform(moderator, "§8--- Moderation Mode & Vanish ---");
            inform(moderator, " §dModeration Mode §etoggled §aON§e.");
            inform(moderator, " §dModeration Mode §etoggled §cOFF§e.");
            inform(moderator, " §dVanish §etoggled §aON§e.");
            inform(moderator, " §dVanish §etoggled §cOFF§e.");
        }

        if (category.equals("all") || category.equals("chat")) {
            inform(moderator, "§8--- Staff Chat & SocialSpy ---");
            inform(moderator, " §d§lSTAFF! §9Steve§f§l » §bHello staff team!");
            inform(moderator, " §6§lSPY! §fSteve §d➟ §fAlex §7§l» §dHey check out this secret base!");
            inform(moderator, " §6Social Spy §etoggled §aON§e.");
            inform(moderator, " §6Social Spy §etoggled §cOFF§e.");
        }

        if (category.equals("all") || category.equals("tp")) {
            inform(moderator, "§8--- Teleportation & Targeting ---");
            InteractiveMessage.text(" §eTeleported to §dAlex§e. ")
                    .hoverAndExecute("§3[INFO]", "§fClick to view information about §dAlex§f.", "/userinfo Alex")
                    .send(moderator);

            InteractiveMessage.text(" §eTeleported §dAlex§e to you. ")
                    .hoverAndExecute("§3[INFO]", "§fClick to view information about §dAlex§f.", "/userinfo Alex")
                    .send(moderator);

            InteractiveMessage.text(" §eRandomly teleported to §dAlex§e. ")
                    .hoverAndExecute("§3[INFO]", "§fClick to view information about §dAlex§f.", "/userinfo Alex")
                    .send(moderator);

            InteractiveMessage.text(" §eTargeting §dAlex §ewith §fModeration HUD§e. ")
                    .hoverAndExecute("§3[INFO]", "§fClick to view information about §dAlex§f.", "/userinfo Alex")
                    .send(moderator);

            inform(moderator, " §eTarget cleared from §fModeration HUD§e.");
            inform(moderator, " §d§lSTAFF! §9Steve§e teleported to §dAlex§e.");
        }

        if (category.equals("all") || category.equals("reports")) {
            inform(moderator, "§8--- Reports & Alerts ---");
            InteractiveMessage.text(" §6§lALERT! §dAlex §ewas reported. ")
                    .hoverAndExecute("§6[DETAILS]", "§fClick to view §dreport §fdetails.", "/checkreport <id>")
                    .append(" ")
                    .hoverAndExecute("§b[MTP]", "§fClick to teleport to §dAlex§f.", "/mtp Alex")
                    .append(" ")
                    .hoverAndExecute("§3[INFO]", "§fClick to view information about §dAlex§f.", "/userinfo Alex")
                    .send(moderator);

            InteractiveMessage.text(" §6§lALERT! §fSteve §ereported §dAlex&e's chat message: §7\"J'vous baise vous grand mort\"§e. ")
                    .hoverAndExecute("§b[MTP]", "§fClick to teleport to §dAlex§f.", "/mtp Alex")
                    .append(" ")
                    .hoverAndExecute("§3[INFO]", "§fClick to view information about §dAlex§f.", "/userinfo Alex")
                    .send(moderator);

            inform(moderator, " §6§lALERT! §dAlex§e was automatically banned for §fdisconnecting §ewhile being frozen by a moderator.");
        }

        if (category.equals("all") || category.equals("sanctions")) {
            inform(moderator, "§8--- Punishments & Sanctions ---");
            inform(moderator, " §d§lSTAFF! §9Steve§e banned §9Alex§e for §61d§e for §cFly hacking§e.");
            inform(moderator, " §d§lSTAFF! §9Steve§e unbanned §9Alex§e.");
            inform(moderator, " §d§lSTAFF! §9Steve§e IP banned §9Alex§e for §cAlting§e.");
            inform(moderator, " §d§lSTAFF! §9Steve§e unbanned IP for §dAlex§e.");
            inform(moderator, " §d§lSTAFF! §9Steve§e muted §dAlex §efor §61h §efor §cSpamming§e.");
            inform(moderator, " §d§lSTAFF! §9Steve§e unmuted §dAlex§e.");
            inform(moderator, " §d§lSTAFF! §9Steve§e kicked §dAlex§e for §cGriefin§e.");
            inform(moderator, "§b ❄ §eYou froze §dAlex§e.");
            inform(moderator, "§b ❄ §eYou unfroze §dAlex§e.");
        }

        if (category.equals("all") || category.equals("settings")) {
            inform(moderator, "§8--- Chat Settings & User Info ---");
            inform(moderator, " §d§lSTAFF! §9Steve §eset §fChat Slowmode §eto §610s§e.");
            inform(moderator, " §d§lSTAFF! §9Steve §ehas disabled §fChat Slowmode§e.");
            inform(moderator, " §7You are now in the §dSTAFF §7channel.");
        }

        if (category.equals("all") || category.equals("target") || category.equals("user") || category.equals("player")) {
            inform(moderator, "§8--- Target / Player Facing Messages ---");
            inform(moderator, " §c§lMUTED! §7You have been muted for §61h §7for: §fSpamming");
            inform(moderator, " §c§lMUTED! §7You are temporarily muted for: §fSpamming");
            inform(moderator, " §a§lUNMUTED! §7You are no longer muted.");
            inform(moderator, "§b ❄ §c§lYOU HAVE BEEN FROZEN BY A MODERATOR!");
            inform(moderator, " §7Do not log out or you will be permanently banned.");
            inform(moderator, "§b ❄ §cYou cannot run commands while frozen.");
            inform(moderator, "§b ❄ §cYou cannot attack while frozen.");
            inform(moderator, "§b ❄ §a§lYOU HAVE BEEN UNFROZEN!");
            inform(moderator, " §7You are now free to move again.");
            inform(moderator, " §c§lSLOWMODE! §7Please wait §e5s §7before chatting again.");
            inform(moderator, " §a§lREPORT SENT! §7Your report for §dAlex §7has been submitted to online staff.");
        }

        inform(moderator, "§e=== [End of Formatting Test] ===");
        return true;
    }
}
