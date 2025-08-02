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

  public ActionBarManager(BlightedPlayer player) {
    this.player = player;
  }

  @Override
  public void run() {
    tick();
  }

  public void tick() {
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
    return "§d" + favors + "☤ Favors";
  }

  private String getManaComponent() {
    double current = player.getMana().getCurrentMana();
    double max = player.getMana().getMaxMana();
    return "§b" + Formatter.formatDouble(current, 0) + "/" + Formatter.formatDouble(max, 0) + "✎ Mana";
  }
}
