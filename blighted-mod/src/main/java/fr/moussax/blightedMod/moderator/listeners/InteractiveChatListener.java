package fr.moussax.blightedMod.moderator.listeners;

import fr.moussax.bedrock.text.InteractiveMessage;
import fr.moussax.blightedMod.moderator.ModerationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class InteractiveChatListener implements Listener {

    private final ModerationManager moderationManager;

    public InteractiveChatListener(ModerationManager moderationManager) {
        this.moderationManager = moderationManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNormalPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player sender = event.getPlayer();
        String chatMessage = event.getMessage();
        String messageColor = moderationManager.isInModerationMode(sender) ? "§b" : "§f";

        InteractiveMessage interactiveMessage = InteractiveMessage.text(" ")
                .hoverAndExecute("§c⚑ ", "§cClick to report §d" + sender.getName() + "§c.",
                        "/report chat " + sender.getName() + " " + chatMessage
                )
                .append("§7" + sender.getName() + " §8§l» " + messageColor + chatMessage);

        for (Player recipient : event.getRecipients()) {
            interactiveMessage.send(recipient);
        }
    }
}
