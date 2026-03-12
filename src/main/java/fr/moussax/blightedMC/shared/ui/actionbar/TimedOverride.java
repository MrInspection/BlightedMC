package fr.moussax.blightedMC.shared.ui.actionbar;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;

public abstract class TimedOverride implements ActionBarOverride {
    private final int priority;
    private long expiresAt = 0;

    protected TimedOverride(int priority) {
        this.priority = priority;
    }

    protected abstract String resolveContent(BlightedPlayer player);

    public void activate(long durationMillis) {
        this.expiresAt = System.currentTimeMillis() + durationMillis;
    }

    public void deactivate() {
        this.expiresAt = 0;
    }

    @Override
    public boolean isActive(BlightedPlayer player) {
        return System.currentTimeMillis() < expiresAt;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String resolve(BlightedPlayer player) {
        return resolveContent(player);
    }
}
