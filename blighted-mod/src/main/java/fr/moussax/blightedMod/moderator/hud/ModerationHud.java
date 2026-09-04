package fr.moussax.blightedMod.moderator.hud;

import fr.moussax.bedrock.ui.actionbar.ActionbarSection;
import fr.moussax.blightedMod.moderator.BlightedModerator;
import fr.moussax.blightedMod.moderator.ModerationManager;
import org.bukkit.entity.Player;

import java.util.Locale;

public final class ModerationHud {
    public static final String SECTION_ID = "moderation_hud";
    public static final int PRIORITY = 100;

    private ModerationHud() {
    }

    public static ActionbarSection createSection(ModerationManager moderationManager) {
        return ActionbarSection.of(SECTION_ID, PRIORITY,
                player -> render(moderationManager, player),
                moderationManager::isModerator
        );
    }

    private static String render(ModerationManager moderationManager, Player player) {
        BlightedModerator moderator = moderationManager.getModerator(player);
        if (moderator == null) {
            return null;
        }

        Player target = moderator.getTargetPlayer();
        if (target == null || !target.isOnline()) {
            return null;
        }

        int healthPercent = (int) Math.round((target.getHealth() / target.getHealth()) * 100.0);

        String distanceText = player.getWorld().equals(target.getWorld())
                ? String.format(Locale.ROOT, "%.2f blocks", player.getLocation().distance(target.getLocation()))
                : "N/A";

        String frozenText = moderationManager.isFrozen(target) ? "§b§lYUP!" : "§c§lNOPE!";
        String pingText = target.getPing() < 100 ? "§a" + target.getPing() + "ms"
                : target.getPing() < 200 ? "§e" + target.getPing() + "ms"
                : target.getPing() < 300 ? "§6" + target.getPing() + "ms"
                : target.getPing() < 500 ? "§c" + target.getPing() + "ms"
                : "§4" + target.getPing() + "ms";

        return "§fTarget: §d" + target.getName()
                + "     §fHP: §d" + healthPercent + "%"
                + "     §fDistance: §d" + distanceText
                + "     §fPing: " + pingText
                + "     §fFrozen: " + frozenText;
    }
}
