package fr.moussax.blightedMC.shared.ui.actionbar;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;

public interface ActionBarOverride {
    String resolve(BlightedPlayer player);
    boolean isActive(BlightedPlayer player);
    int getPriority();
}
