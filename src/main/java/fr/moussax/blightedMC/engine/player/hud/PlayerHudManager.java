package fr.moussax.blightedMC.engine.player.hud;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.ui.actionbar.ActionbarSection;
import fr.moussax.blightedMC.shared.ui.actionbar.ActionbarService;
import fr.moussax.blightedMC.utils.Formatter;
import org.jspecify.annotations.NonNull;

public final class PlayerHudManager {

    public static final String SECTION_GEMS = "gems";
    public static final String SECTION_MANA = "mana";

    private final ActionbarService actionBarService;

    public PlayerHudManager(@NonNull ActionbarService actionBarService) {
        this.actionBarService = actionBarService;
        initializeDefaultSections();
    }

    private void initializeDefaultSections() {
        actionBarService.registerSection(ActionbarSection.of(SECTION_GEMS, 0, player -> {
            BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
            if (blightedPlayer == null) return null;
            return "§d" + Formatter.formatDecimalWithCommas(blightedPlayer.getGemsManager().getGems()) + "✵ Gems";
        }));

        actionBarService.registerSection(ActionbarSection.of(SECTION_MANA, 10, player -> {
            BlightedPlayer blightedPlayer = BlightedPlayer.getBlightedPlayer(player);
            if (blightedPlayer == null) return null;
            var mana = blightedPlayer.getMana();
            return "§b" + Formatter.formatDouble(mana.getCurrentMana(), 0) + "/"
                    + Formatter.formatDouble(mana.getMaxMana(), 0) + "✎ Mana";
        }));
    }
}
