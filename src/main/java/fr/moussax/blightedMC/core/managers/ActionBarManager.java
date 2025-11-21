package fr.moussax.blightedMC.core.managers;

import fr.moussax.blightedMC.core.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.formatting.Formatter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Manages the display of the action bar for a BlightedPlayer.
 * <p>
 * Periodically updates health, favors, and mana information,
 * including handling insufficient mana warnings.
 */
public class ActionBarManager implements Runnable {
    private final BlightedPlayer player;
    private boolean insufficientMana = false;
    private long insufficientManaStartTime = 0;
    private static final long INSUFFICIENT_MANA_DURATION = 1000; // 1 second in milliseconds

    public ActionBarManager(BlightedPlayer player) {
        this.player = player;
    }

    public void setInsufficientMana(boolean insufficient) {
        this.insufficientMana = insufficient;
        if (insufficient) {
            this.insufficientManaStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public void run() {
        tick();
    }

    public void tick() {
        player.getMana().regenerateMana();

        if (insufficientMana && System.currentTimeMillis() - insufficientManaStartTime > INSUFFICIENT_MANA_DURATION) {
            insufficientMana = false;
        }

        String separator = " §8- ";
        String gemsComponent = getFavorsComponent();
        String manaComponent = separator + getManaComponent();

        player.getPlayer().spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent("§8■ " + gemsComponent + manaComponent + " §8■")
        );
    }

    private String getFavorsComponent() {
        int gems = player.getGems().getGems();
        return "§7Gems: §e" + gems + "✵";
    }

    private String getManaComponent() {
        if (insufficientMana) {
            return "§c§lNOT ENOUGH MANA!";
        }
        double current = player.getMana().getCurrentMana();
        double max = player.getMana().getMaxMana();
        return "§7Mana: §b" + Formatter.formatDouble(current, 0) + "§8/§b" + Formatter.formatDouble(max, 0);
    }
}
