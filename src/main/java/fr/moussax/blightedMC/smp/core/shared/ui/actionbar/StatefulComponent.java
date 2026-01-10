package fr.moussax.blightedMC.smp.core.shared.ui.actionbar;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;

public abstract class StatefulComponent extends BaseComponent {
    private String notification;
    private long notificationExpiresAt = 0;

    protected StatefulComponent(String id, int priority) {
        super(id, priority);
    }

    protected abstract String resolveContent(BlightedPlayer player);

    public void setNotification(String message, long durationMillis) {
        this.notification = message;
        this.notificationExpiresAt = System.currentTimeMillis() + durationMillis;
    }

    public void clearNotification() {
        this.notification = null;
        this.notificationExpiresAt = 0;
    }

    @Override
    public String resolve(BlightedPlayer player) {
        if (System.currentTimeMillis() < notificationExpiresAt && notification != null) {
            return notification;
        }
        return resolveContent(player);
    }
}
