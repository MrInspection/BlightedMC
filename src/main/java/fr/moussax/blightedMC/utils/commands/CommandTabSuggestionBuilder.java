package fr.moussax.blightedMC.utils.commands;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.registry.EntitiesRegistry;
import fr.moussax.blightedMC.core.items.ItemTemplate;
import fr.moussax.blightedMC.core.items.registry.ItemDirectory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Dynamic tab-completion builder that generates command suggestions based on
 * {@link CommandArgument} annotations present on a command executor class.
 * <p>
 * Supports special placeholders:
 * <ul>
 *   <li><b>$players</b> – Suggests all online players.</li>
 *   <li><b>$entities</b> – Suggests all registered {@link BlightedEntity} IDs.</li>
 * </ul>
 */
public class CommandTabSuggestionBuilder implements TabCompleter {
    public final HashMap<ArgumentRule, List<String>> rules = new LinkedHashMap<>();

    /**
     * Constructs a tab suggestion builder for a given command class.
     *
     * @param commandClass the command executor class annotated with {@link CommandArgument}
     * @throws IllegalArgumentException if the class lacks the required annotation
     */
    public CommandTabSuggestionBuilder(Class<?> commandClass) {
        var annotations = commandClass.getAnnotationsByType(CommandArgument.class);
        if (annotations.length == 0) {
            throw new IllegalArgumentException("Missing @CommandArgument annotation.");
        }

        for (var argument : annotations) {
            rules.put(new ArgumentRule(argument.position(), argument.after()), List.of(argument.suggestions()));
        }
    }

    /**
     * Provides tab completion suggestions based on the current command input and predefined rules.
     *
     * @param sender  the entity requesting tab completion
     * @param command the command being executed
     * @param label   the command alias used
     * @param args    the arguments currently typed
     * @return a list of matching suggestions, or an empty list if none apply
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        for (var entry : rules.entrySet()) {
            if (entry.getKey().matches(args)) {
                List<String> suggestions = entry.getValue();

                if (suggestions.size() == 1 && suggestions.getFirst().equals("$players")) {
                    return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList();
                }

                if (suggestions.size() == 1 && suggestions.getFirst().equals("$items")) {
                    return ItemDirectory.getAllItems().stream()
                        .map(ItemTemplate::getItemId)
                        .toList();
                }

                if (suggestions.size() == 1 && suggestions.getFirst().equals("$entities")) {
                    return EntitiesRegistry.getAllEntities().stream()
                        .map(BlightedEntity::getEntityId)
                        .toList();
                }

                return suggestions;
            }
        }
        return Collections.emptyList();
    }

    /**
     * Internal rule representation for tab-completion behavior.
     * <p>
     * A rule matches a specific argument position and may optionally require
     * that the preceding argument matches one of a set of keywords.
     */
    private static class ArgumentRule {
        private final int position;
        private final Set<String> after;

        /**
         * Creates a new argument rule.
         *
         * @param position the zero-based index of the argument this rule applies to
         * @param after    array of allowed preceding arguments; empty means no restriction
         */
        ArgumentRule(int position, String[] after) {
            this.position = position;
            this.after = Set.of(after);
        }

        /**
         * Checks whether the current user input satisfies this rule.
         *
         * @param input the current command arguments
         * @return true if this rule should provide suggestions, false otherwise
         */
        boolean matches(String[] input) {
            if (input.length != position + 1) return false;
            return after.isEmpty() || after.contains(input[position - 1]);
        }
    }
}
