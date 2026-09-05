package fr.moussax.blightedMod.moderator.hud;

import fr.moussax.bedrock.ui.actionbar.ActionbarSection;
import fr.moussax.blightedMod.moderator.BlightedModerator;
import fr.moussax.blightedMod.moderator.ModerationManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Objects;

public final class ModerationHud {
    public static final String SECTION_ID = "moderation_hud";
    public static final int PRIORITY = 100;

    private ModerationHud() {
    }

    public static ActionbarSection createSection(ModerationManager moderationManager) {
        return ActionbarSection.exclusiveOf(SECTION_ID, PRIORITY,
                player -> render(moderationManager, player),
                moderationManager::isInModerationMode
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

        double maxHealth = Objects.requireNonNull(target.getAttribute(Attribute.MAX_HEALTH)).getValue();
        int healthPercent = (int) Math.round((target.getHealth() / maxHealth) * 100.0);
        String healthColor = healthPercent >= 60 ? "§a" : healthPercent >= 30 ? "§e" : "§c";

        String distanceText = player.getWorld().equals(target.getWorld())
                ? String.format(Locale.ROOT, "%.2f blocks", player.getLocation().distance(target.getLocation()))
                : "N/A";

        String frozenText = moderationManager.isFrozen(target) ? "§b§lYUP!" : "§c§lNOPE!";
        int ping = target.getPing();
        String pingColor = ping < 100 ? "§a" : ping < 200 ? "§e" : ping < 300 ? "§6" : ping < 500 ? "§c" : "§4";
        String pingText = pingColor + ping + "ms";

        return "§fTarget: §d" + target.getName()
                + "     §fHP: " + healthColor + healthPercent + "%"
                + "     §fDistance: §d" + distanceText
                + "     §fPing: " + pingText
                + "     §fFrozen: " + frozenText;
    }
}
