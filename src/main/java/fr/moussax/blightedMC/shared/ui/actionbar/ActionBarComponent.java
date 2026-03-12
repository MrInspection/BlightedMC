package fr.moussax.blightedMC.shared.ui.actionbar;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import org.jspecify.annotations.Nullable;

public interface ActionBarComponent {
    String getId();
    @Nullable String resolve(BlightedPlayer player);
    int getPriority();
    boolean shouldDisplay(BlightedPlayer player);
}
