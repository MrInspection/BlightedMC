package fr.moussax.blightedMC.smp.core.shared.ui.actionbar.overrides;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.player.mod.ModerationManager;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.ActionBarOverride;

import java.util.Objects;

public class ModeratorStatusOverride implements ActionBarOverride {
    @Override
    public boolean isActive(BlightedPlayer player) {
        if (!player.isModerator()) {
            return false;
        }
        return Objects.requireNonNull(player.getModerator()).isModerationMode();
    }

    @Override
    public int getPriority() {
        return 500;
    }

    @Override
    public String resolve(BlightedPlayer player) {
        int vanishedCount = ModerationManager.getInstance().getVanishedCount();
        assert player.getModerator() != null;
        boolean isVanished = player.getModerator().isVanished();

        String vanishStatus = isVanished ? "§a§lVANISHED" : "§c§lVISIBLE";
        String vanishCountInfo = vanishedCount > 0 ? " §8(" + vanishedCount + " vanished)" : "";

        return String.format(
            "§fMode: §9§lMODERATOR     §fStatus: %s%s",
            vanishStatus,
            vanishCountInfo
        );
    }
}
