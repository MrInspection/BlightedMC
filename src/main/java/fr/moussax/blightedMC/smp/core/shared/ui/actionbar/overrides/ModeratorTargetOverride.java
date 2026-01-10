package fr.moussax.blightedMC.smp.core.shared.ui.actionbar.overrides;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.player.mod.ModerationManager;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.ActionBarOverride;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Objects;

public class ModeratorTargetOverride implements ActionBarOverride {
    private Player target;

    public void setTarget(Player target) {
        this.target = target;
    }

    public void clearTarget() {
        this.target = null;
    }

    @Override
    public boolean isActive(BlightedPlayer player) {
        if (target == null || !target.isOnline()) {
            clearTarget();
            return false;
        }
        return true;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public String resolve(BlightedPlayer player) {
        double healthPercent = (target.getHealth() / Objects.requireNonNull(target.getAttribute(Attribute.MAX_HEALTH)).getValue()) * 100;
        String healthColor = healthPercent > 50 ? "§a" : (healthPercent > 20 ? "§e" : "§c");

        String distanceStr;
        if (player.getPlayer().getWorld().equals(target.getWorld())) {
            double dist = player.getPlayer().getLocation().distance(target.getLocation());
            distanceStr = Formatter.formatDouble(dist, 2) + "m";
        } else {
            distanceStr = "§cDiff. World";
        }

        boolean isFrozen = ModerationManager.getInstance().isFrozen(target);
        String frozenStr = isFrozen ? "§b§lYEP" : "§c§lNOPE";

        return String.format(
            "§fTarget: §a%s     §fHealth: %s%d%%     §fDistance: §a%s     §fFrozen: %s",
            target.getName(),
            healthColor, (int) healthPercent,
            distanceStr,
            frozenStr
        );
    }
}
