package fr.moussax.blightedMC.utils.formatting;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility for building and displaying formatted command help messages.
 *
 * <p>Provides structured command documentation with syntax highlighting
 * for required parameters {@code <param>}, optional parameters {@code [param]},
 * and command tokens.
 *
 * <p>Example usage:
 * <pre>{@code
 * CommandInfo.send(player, "Item Commands", "Manage custom items",
 *   CommandInfo.Entry.of("Give yourself an item", "item", "give", "<item>", "[amount]"),
 *   CommandInfo.Entry.of("List all items", "item", "list")
 * );
 * }</pre>
 */
public final class CommandInfo {
  private static final String HEADER_COLOR = "§e§l";
  private static final String SEPARATOR_COLOR = "§f";
  private static final String DESCRIPTION_COLOR = "§7";
  private static final String BULLET = "§f  • §e/";
  private static final String USAGE_BULLET = "§f  • §7Usage: §e/";
  private static final String DESC_BULLET = "§f  • §7Description: §r";
  private static final String ARROW = " §f§l» §7";
  private static final String PARAM_COLOR = "§e";
  private static final String INNER_COLOR = "§f";

  private CommandInfo() {}

  /**
   * Represents a single command entry with tokens and description.
   *
   * @param tokens command tokens (base command followed by arguments)
   * @param description human-readable description of the command
   */
  public record Entry(@Nonnull List<String> tokens, @Nonnull String description) {
    public Entry {
      if (tokens.isEmpty()) {
        throw new IllegalArgumentException("Command tokens cannot be empty");
      }
    }

    /**
     * Creates a command entry from description and tokens.
     *
     * @param description command description
     * @param tokens command tokens (first token is the base command)
     * @return new command entry
     */
    public static Entry of(@Nonnull String description, @Nonnull String... tokens) {
      return new Entry(Arrays.asList(tokens), description);
    }
  }

  /**
   * Builds formatted help message lines from command entries.
   *
   * @param title help section title
   * @param description section description
   * @param entries command entries to display
   * @return list of formatted message lines
   */
  @Nonnull
  public static List<String> buildHelpLines(
    @Nonnull String title,
    @Nonnull String description,
    @Nonnull List<Entry> entries
  ) {
    List<String> lines = new ArrayList<>();
    lines.add(" ");
    lines.add(" ");
    lines.add("§r    " + HEADER_COLOR + title + " " + SEPARATOR_COLOR + "| " + DESCRIPTION_COLOR + description);
    lines.add(" ");

    for (Entry entry : entries) {
      if (entry == null) {
        continue;
      }
      lines.add(formatCommandEntry(entry));
    }

    lines.add(" ");
    return lines;
  }

  /**
   * Builds formatted help message for a single command with usage and description.
   *
   * @param usage command usage tokens (base command followed by arguments)
   * @param description command description
   * @return list of formatted message lines
   */
  @Nonnull
  public static List<String> buildCommandUsageHelp(@Nonnull String description, @Nonnull String... usage) {
    List<String> lines = new ArrayList<>();
    lines.add(" ");
    lines.add("§r    " + HEADER_COLOR + "HELP " + SEPARATOR_COLOR + "| " + DESCRIPTION_COLOR + "Command Information");

    StringBuilder usageLine = new StringBuilder(USAGE_BULLET);
    usageLine.append(INNER_COLOR).append(stripColorCodes(usage[0]));
    for (int i = 1; i < usage.length; i++) {
      usageLine.append(" ").append(formatToken(usage[i]));
    }
    lines.add(usageLine.toString());

    lines.add(DESC_BULLET + description);
    lines.add(" ");

    return lines;
  }

  /**
   * Sends formatted help messages to a player.
   *
   * @param player recipient player
   * @param title help section title
   * @param description section description
   * @param entries command entries to display
   */
  public static void sendCommands(
    @Nonnull Player player,
    @Nonnull String title,
    @Nonnull String description,
    @Nonnull List<Entry> entries
  ) {
    buildHelpLines(title, description, entries).forEach(player::sendMessage);
  }

  /**
   * Sends formatted help messages to a player.
   *
   * @param player recipient player
   * @param title help section title
   * @param description section description
   * @param entries command entries to display
   */
  public static void sendCommands(
    @Nonnull Player player,
    @Nonnull String title,
    @Nonnull String description,
    @Nonnull Entry... entries
  ) {
    sendCommands(player, title, description, Arrays.asList(entries));
  }

  /**
   * Sends formatted single command help to a player.
   *
   * @param player recipient player
   * @param description command description
   * @param usage command usage tokens (base command followed by arguments)
   */
  public static void sendUsage(
    @Nonnull Player player,
    @Nonnull String description,
    @Nonnull String... usage
  ) {
    buildCommandUsageHelp(description, usage).forEach(player::sendMessage);
  }

  private static String formatCommandEntry(@Nonnull Entry entry) {
    StringBuilder builder = new StringBuilder(BULLET);
    builder.append(INNER_COLOR).append(stripColorCodes(entry.tokens.getFirst()));

    for (int i = 1; i < entry.tokens.size(); i++) {
      builder.append(" ").append(formatToken(entry.tokens.get(i)));
    }

    builder.append(ARROW).append(entry.description);
    return builder.toString();
  }

  private static String stripColorCodes(@Nonnull String token) {
    return token.replaceAll("§.", "");
  }

  private static String formatToken(@Nonnull String token) {
    if (token.startsWith("<") && token.endsWith(">")) {
      return formatParameter(token, '<', '>');
    }
    if (token.startsWith("[") && token.endsWith("]")) {
      return formatParameter(token, '[', ']');
    }
    return PARAM_COLOR + stripColorCodes(token);
  }

  private static String formatParameter(@Nonnull String token, char open, char close) {
    String inner = token.substring(1, token.length() - 1);
    return PARAM_COLOR + open + INNER_COLOR + inner + PARAM_COLOR + close;
  }
}