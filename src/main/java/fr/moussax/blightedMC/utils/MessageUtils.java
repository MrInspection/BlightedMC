package fr.moussax.blightedMC.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class MessageUtils {
  private static final String ADMIN_PERMISSION = "blightedmc.admin";

  /**
   * Inform a player or console with a list of messages
   *
   * @param sender the player or console being informed
   * @param messages the list of messages
   */
  public static void informSender(CommandSender sender, List<String> messages){
    for (String message : messages) {
      sender.sendMessage("§8 ■ §7" + message);
    }
  }

  /**
   * Inform a player or console with a list of messages
   *
   * @param sender the player or console being informed
   * @param messages the list of messages
   */
  public static void informSender(CommandSender sender, String... messages){
    for (String message : messages) {
      sender.sendMessage(message);
    }
  }

  /**
   * Inform a player or console with a single message
   * @param sender the player or console being informed
   * @param message the message
   */
  public static void informSender(CommandSender sender, String message) {
    informSender(sender, Collections.singletonList(message));
  }

  /**
   * Warn a player or console with a list of messages, plays an error sound and uses red text
   *
   * @param sender the player or console being warned
   * @param messages the messages
   */
  public static void warnSender(CommandSender sender, List<String> messages) {
    if(sender instanceof Player p) {
      p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
    }
    for (String message : messages) {
      sender.sendMessage("§4 ■ §c" + message);
    }
  }

  /**
   * Warn a player or console with a list of messages, plays an error sound.
   * Warning: No color formatting applied with this method
   *
   * @param sender the player or console being warned
   * @param messages the messages
   */
  public static void warnSender(CommandSender sender, String... messages) {
    if(sender instanceof Player p) {
      p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 100f, 0.5f);
    }
    for (String message : messages) {
      sender.sendMessage(message);
    }
  }

  /**
   * Warn a player or console with a single message, plays an error sound and uses red text
   *
   * @param sender the player or console being warned
   * @param message the message
   */
  public static void warnSender(CommandSender sender, String message) {
    warnSender(sender, Collections.singletonList(message));
  }

  /**
   * Verifies whether the player has admin power.
   * <p>
   * A player is considered an admin if they are an operator (OP) and have
   * the {@link #ADMIN_PERMISSION} permission. If the player is not an admin,
   * a warning message is sent to the player.
   *
   * @param sender the player executing the command
   * @return {@code true} if the player has admin permission, {@code false} otherwise
   */
  public static boolean enforceAdminPermission(Player sender) {
    if (!sender.isOp() || !sender.hasPermission(ADMIN_PERMISSION)) {
      MessageUtils.warnSender(sender, "You must be an §4ADMIN §cto use this command.");
      return false;
    }
    return true;
  }

  /**
   * Creates a {@link TextComponent} with hover and optional click interactions.
   * <p>
   * - Hovering the text will display the provided hover message.<br>
   * - If a click action is provided, clicking the text will trigger the specified action.
   * </p>
   *
   * @param text       the visible text to display in chat
   * @param hoverText  the text displayed when the user hovers over this component
   * @param action     the {@link ClickEvent.Action} to execute on click, or {@code null} for none
   * @param actionText the string used by the click event (e.g., command or URL), or {@code null} if no action
   * @return a {@link TextComponent} with the given hover and optional click behavior
   */
  public static TextComponent createClickableText(String text, String hoverText, @Nullable ClickEvent.Action action, @Nullable String actionText) {
    TextComponent component = new TextComponent(text);
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
    component.setClickEvent(new ClickEvent(action, actionText));
    return component;
  }
}

