package fr.moussax.blightedMC.smp.core.shared.ui.actionbar;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;

public interface ActionBarOverride {
    String resolve(BlightedPlayer player);
    boolean isActive(BlightedPlayer player);
    int getPriority();
}
