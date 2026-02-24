package fr.moussax.blightedMC.smp.core.shared.ui.actionbar;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.components.GemsComponent;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.components.ManaComponent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarManager implements Runnable {
    private final BlightedPlayer player;
    private final ActionBarComposer composer = new ActionBarComposer();

    public ActionBarManager(BlightedPlayer player) {
        this.player = player;
        initializeComponents();
    }

    private void initializeComponents() {
        composer.registerComponent(new GemsComponent());
        composer.registerComponent(new ManaComponent());
    }

    public void setInsufficientMana(boolean insufficient) {
        ManaComponent manaComponent = (ManaComponent) composer.getComponent("mana");
        if (manaComponent != null && insufficient) {
            manaComponent.setNotification("§c§lNOT ENOUGH MANA!", 1000);
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
