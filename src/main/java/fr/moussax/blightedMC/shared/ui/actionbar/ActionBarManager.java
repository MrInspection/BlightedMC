package fr.moussax.blightedMC.shared.ui.actionbar;

import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.shared.ui.actionbar.components.GemsComponent;
import fr.moussax.blightedMC.shared.ui.actionbar.components.ManaComponent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarManager implements Runnable {
    private final BlightedPlayer player;
    private final ActionBarComposer composer = new ActionBarComposer();

    private final TextNotificationOverride notificationOverride = new TextNotificationOverride(100);

    public ActionBarManager(BlightedPlayer player) {
        this.player = player;
        initializeComponents();
    }

    private void initializeComponents() {
        composer.registerComponent(new GemsComponent());
        composer.registerComponent(new ManaComponent());
        composer.setOverride("notification", notificationOverride);
    }

    public void setNotification(String message, long durationMillis) {
        notificationOverride.show(message, durationMillis);
        tick();
    }

    public void setInsufficientMana(boolean insufficient) {
        ManaComponent manaComponent = (ManaComponent) composer.getComponent("mana");
        if (manaComponent != null && insufficient) {
            manaComponent.setNotification("§c§lNOT ENOUGH MANA!", 1000);
        }
    }

    private static final class TextNotificationOverride extends TimedOverride {
        private String message;

        private TextNotificationOverride(int priority) {
            super(priority);
        }

        public void show(String message, long durationMillis) {
            this.message = message;
            activate(durationMillis);
        }

        @Override
        protected String resolveContent(BlightedPlayer player) {
            return message;
        }
    }

    @Override
    public void run() {
        tick();
    }

    public void tick() {
        player.getMana().regenerateMana();
        String actionBar = composer.compose(player);
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBar));
    }
}
