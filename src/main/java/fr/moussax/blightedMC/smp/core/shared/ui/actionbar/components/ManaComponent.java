package fr.moussax.blightedMC.smp.core.shared.ui.actionbar.components;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.StatefulComponent;
import fr.moussax.blightedMC.utils.formatting.Formatter;

public class ManaComponent extends StatefulComponent {
    public ManaComponent() {
        super("mana", 10);
    }

    @Override
    protected String resolveContent(BlightedPlayer player) {
        double current = player.getMana().getCurrentMana();
        double max = player.getMana().getMaxMana();
        return "§b" + Formatter.formatDouble(current, 0) + "/" + Formatter.formatDouble(max, 0) + "✎ Mana";
    }
}
