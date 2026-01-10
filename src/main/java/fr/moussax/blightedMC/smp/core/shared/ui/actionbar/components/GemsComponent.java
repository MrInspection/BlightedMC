package fr.moussax.blightedMC.smp.core.shared.ui.actionbar.components;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.BaseComponent;
import fr.moussax.blightedMC.utils.formatting.Formatter;

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