package fr.moussax.blightedMC.smp.core.shared.ui.actionbar;

import fr.moussax.blightedMC.smp.core.player.BlightedPlayer;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.components.GemsComponent;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.components.ManaComponent;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.overrides.ModeratorStatusOverride;
import fr.moussax.blightedMC.smp.core.shared.ui.actionbar.overrides.ModeratorTargetOverride;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBarManager implements Runnable {
    private final BlightedPlayer player;
    private final ActionBarComposer composer = new ActionBarComposer();
    private final ModeratorTargetOverride moderatorTargetOverride = new ModeratorTargetOverride();
    private final ModeratorStatusOverride moderatorStatusOverride = new ModeratorStatusOverride();

    public ActionBarManager(BlightedPlayer player) {
        this.player = player;
        initializeComponents();
    }

    private void initializeComponents() {
        composer.registerComponent(new GemsComponent());
        composer.registerComponent(new ManaComponent());
        composer.setOverride("moderator_status", moderatorStatusOverride);
        composer.setOverride("moderator_target", moderatorTargetOverride);
    }

    public void setModTarget(Player target) {
        moderatorTargetOverride.setTarget(target);
    }

    public void clearModTarget() {
        moderatorTargetOverride.clearTarget();
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
