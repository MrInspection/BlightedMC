package fr.moussax.blightedMC.commands.utils;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility for formatting and sending command help messages.
 *
 * <p>Supports visual formatting for command syntax:
 * <ul>
 *     <li>{@code <argument>} target or reference arguments</li>
 *     <li>{@code [argument]} value or input arguments</li>
 *     <li>Command literals</li>
 * </ul>
 *
 * <p>The argument notation is only used for display formatting and
 * does not define command validation rules.
 */
public final class CommandFormatter {

    private static final String HEADER_COLOR = "§e§l";
    private static final String SEPARATOR_COLOR = "§f";
    private static final String DESCRIPTION_COLOR = "§7";

    private static final String COMMAND_COLOR = "§f";
    private static final String ARGUMENT_COLOR = "§e";

    private static final String BULLET = "§f  • ";
    private static final String USAGE_PREFIX = "§f  • §7Usage: ";
    private static final String DESCRIPTION_PREFIX = "§f  • §7Description: §r";
    private static final String ARROW = " §f§l» §7";

    private static final String HEADER_FORMAT = "§r    %s%s%s%s";
    private static final Pattern COLOR_PATTERN = Pattern.compile("§.");

    private CommandFormatter() {
    }

    /**
     * Represents a command displayed in a help menu.
     *
     * @param syntax      command syntax without the leading slash
     * @param description short command description
     */
    public record CommandInfo(String syntax, String description) {

        public CommandInfo {
            Objects.requireNonNull(syntax, "syntax");
            Objects.requireNonNull(description, "description");

            if (syntax.isBlank()) {
                throw new IllegalArgumentException("Command syntax cannot be blank.");
            }
        }

        /**
         * Creates command information for help display.
         *
         * @param syntax      command syntax without the leading slash
         * @param description short command description
         * @return command information
         */
        public static CommandInfo of(String syntax, String description) {
            return new CommandInfo(syntax, description);
        }
    }

    /**
     * Sends a formatted command list to a player.
     *
     * @param player      command recipient
     * @param title       help section title
     * @param description help section description
     * @param commands    commands to display
     */
    public static void sendCommands(Player player, String title, String description, CommandInfo... commands) {
        buildCommands(title, description, Arrays.asList(commands)).forEach(player::sendMessage);
    }

    /**
     * Sends formatted usage information for a command.
     *
     * @param player  command recipient
     * @param command command information
     */
    public static void sendUsage(Player player, @NonNull CommandInfo command) {
        buildUsage(command).forEach(player::sendMessage);
    }

    private static List<String> buildCommands(
            String title,
            String description,
            List<CommandInfo> commands
    ) {
        List<String> lines = new ArrayList<>();

        lines.add(" ");
        lines.add(" ");
        lines.add(
                HEADER_FORMAT.formatted(
                        HEADER_COLOR,
                        title,
                        SEPARATOR_COLOR + " | ",
                        DESCRIPTION_COLOR + description
                )
        );
        lines.add(" ");

        for (CommandInfo command : commands) {
            if (command == null) {
                continue;
            }

            lines.add(formatCommand(command));
        }

        lines.add(" ");
        return lines;
    }

    private static List<String> buildUsage(CommandInfo command) {
        List<String> lines = new ArrayList<>();

        lines.add(" ");
        lines.add(
                HEADER_FORMAT.formatted(
                        HEADER_COLOR,
                        "HELP",
                        SEPARATOR_COLOR + " | ",
                        DESCRIPTION_COLOR + "Command Information"
                )
        );
        lines.add(USAGE_PREFIX + formatSyntax(command.syntax()));
        lines.add(DESCRIPTION_PREFIX + command.description());
        lines.add(" ");

        return lines;
    }

    private static String formatCommand(CommandInfo command) {
        return BULLET
                + formatSyntax(command.syntax())
                + ARROW
                + command.description();
    }

    private static String formatSyntax(@NonNull String syntax) {
        StringBuilder builder = new StringBuilder("§e/");
        String[] arguments = syntax.strip().split("\\s+");

        for (int index = 0; index < arguments.length; index++) {
            if (index > 0) {
                builder.append(' ');
            }
            builder.append(formatToken(arguments[index]));
        }
        return builder.toString();
    }

    private static String formatToken(@NonNull String token) {
        token = stripColorCodes(token);

        if (isWrapped(token, '<', '>')) {
            return formatArgument(token, '<', '>');
        }

        if (isWrapped(token, '[', ']')) {
            return formatArgument(token, '[', ']');
        }
        return COMMAND_COLOR + token;
    }

    private static boolean isWrapped(String value, char start, char end) {
        return value.length() > 1
                && value.charAt(0) == start
                && value.charAt(value.length() - 1) == end;
    }

    private static String formatArgument(String value, char start, char end) {
        return ARGUMENT_COLOR
                + start
                + COMMAND_COLOR
                + value.substring(1, value.length() - 1)
                + ARGUMENT_COLOR
                + end;
    }

    private static String stripColorCodes(String value) {
        return COLOR_PATTERN.matcher(value).replaceAll("");
    }
}
