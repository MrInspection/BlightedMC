package fr.moussax.blightedMC.shared.ui.actionbar.components;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.ui.actionbar.BaseComponent;
import fr.moussax.blightedMC.shared.formatting.Formatter;

public class GemsComponent extends BaseComponent {
    public GemsComponent() {
        super("gems", 0);
    }

    @Override
    public String resolve(BlightedPlayer player) {
        int gems = player.getGemsManager().getGems();
        return "§d" + Formatter.formatDecimalWithCommas(gems) + "✵ Gems";
    }
}