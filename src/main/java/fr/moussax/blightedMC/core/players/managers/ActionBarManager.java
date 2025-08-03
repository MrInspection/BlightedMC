package fr.moussax.blightedMC.core.players.managers;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.utils.Formatter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;

import java.util.Objects;

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
    
    String space = "     ";
    String healthComponent = getHealthComponent();
    String favorsComponent = space + getFavorsComponent();
    String manaComponent = space + getManaComponent();

    player.getPlayer().spigot().sendMessage(
        ChatMessageType.ACTION_BAR,
        new TextComponent(healthComponent + favorsComponent + manaComponent)
    );
  }

  private String getHealthComponent() {
    double current = player.getPlayer().getHealth();
    double max = Objects.requireNonNull(Objects.requireNonNull(player.getPlayer()).getAttribute(Attribute.MAX_HEALTH)).getValue();
    if (current > max) current = max;

    return ChatColor.RED + Formatter.formatDouble(current, 1) + "/" + Formatter.formatDouble(max, 1) + "❤";
  }

  private String getFavorsComponent() {
    int favors = player.getFavors().getFavors();
    return "§6" + favors + "✵ Favors";
  }

  private String getManaComponent() {
    if (insufficientMana) {
      return "§c§lNOT ENOUGH MANA!";
    }
    double current = player.getMana().getCurrentMana();
    double max = player.getMana().getMaxMana();
    return "§b" + Formatter.formatDouble(current, 0) + "/" + Formatter.formatDouble(max, 0) + "✎ Mana";
  }
}
