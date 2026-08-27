package fr.moussax.blightedMC.engine.player.hud;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.ui.actionbar.ActionbarSection;
import fr.moussax.blightedMC.shared.ui.actionbar.ActionbarService;
import fr.moussax.blightedMC.shared.text.Formatter;
import org.jspecify.annotations.NonNull;

/**
 * Manages player HUD display registration and periodic action bar formatting.
 */
public final class PlayerHudManager {

    /** Action bar section identifier for player gem balance. */
    public static final String SECTION_GEMS = "gems";
    /** Action bar section identifier for player mana pool. */
    public static final String SECTION_MANA = "mana";

    private final ActionbarService actionBarService;

    /**
     * Constructs a HUD manager and registers default gems and mana display sections.
     *
     * @param actionBarService action bar service managing player displays
     */
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
