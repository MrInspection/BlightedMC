package fr.moussax.blightedMC.smp.core.shared.ui.actionbar;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;

public abstract class BaseComponent implements ActionBarComponent {
    protected final String id;
    protected final int priority;

    protected BaseComponent(String id, int priority) {
        this.id = id;
        this.priority = priority;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean shouldDisplay(BlightedPlayer player) {
        return true;
    }
}
