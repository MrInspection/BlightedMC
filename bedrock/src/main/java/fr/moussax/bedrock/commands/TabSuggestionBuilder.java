package fr.moussax.bedrock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Produces positional tab completions for commands from {@link CommandArgument} declarations.
 */
public final class TabSuggestionBuilder implements TabCompleter {

    private final TabSuggestionRegistry suggestionRegistry;
    private final List<RuleEntry> rules;

    /**
     * Constructs a tab completer by parsing {@link CommandArgument} annotations on a command class.
     *
     * @param commandType        command class annotated with {@link CommandArgument}
     * @param suggestionRegistry registry for resolving dynamic suggestion keys
     * @throws IllegalArgumentException if {@code commandType} lacks {@link CommandArgument} annotations, or contains invalid rule positions or paths
     */
    public TabSuggestionBuilder(Class<?> commandType, TabSuggestionRegistry suggestionRegistry) {
        Objects.requireNonNull(commandType, "commandType");
        this.suggestionRegistry = Objects.requireNonNull(suggestionRegistry, "suggestionRegistry");

        CommandArgument[] arguments = commandType.getAnnotationsByType(CommandArgument.class);

        if (arguments.length == 0) {
            throw new IllegalArgumentException(
                    "Command type '%s' has no @CommandArgument declarations".formatted(commandType.getName())
            );
        }

        List<RuleEntry> configuredRules = new ArrayList<>(arguments.length);

        for (CommandArgument argument : arguments) {
            if (argument.position() < 0) {
                throw new IllegalArgumentException(
                        "Command argument position cannot be negative in: " + commandType.getName()
                );
            }

            if (argument.path().length > argument.position()) {
                throw new IllegalArgumentException(
                        "Path length cannot exceed target position in: " + commandType.getName()
                );
            }

            configuredRules.add(
                    new RuleEntry(
                            new ArgumentRule(argument.position(), List.of(argument.path())),
                            argument.permission(),
                            List.of(argument.suggestions())
                    )
            );
        }

        this.rules = List.copyOf(configuredRules);
    }

    /**
     * Calculates matching tab completion candidates for the specified command sender and argument input.
     *
     * @param sender  source sending the command
     * @param command command executed
     * @param label   alias used to execute the command
     * @param args    current argument array provided by the sender
     * @return sorted list of matching completion strings, or an empty list if no suggestions match
     */
    @Override
    public List<String> onTabComplete(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String label,
            String @NonNull [] args
    ) {
        if (args.length == 0) {
            return List.of();
        }

        Set<String> aggregatedCandidates = new HashSet<>();
        String currentInput = args[args.length - 1];

        for (RuleEntry entry : rules) {
            // ponytail: kept - preserve authorization boundary before calculating completions
            if (!entry.permission().isEmpty() && !sender.hasPermission(entry.permission())) {
                continue;
            }

            if (entry.rule().matches(args)) {
                aggregatedCandidates.addAll(resolveCandidates(entry.suggestions()));
            }
        }

        if (aggregatedCandidates.isEmpty()) {
            return List.of();
        }

        List<String> matchedResults = new ArrayList<>();
        StringUtil.copyPartialMatches(currentInput, aggregatedCandidates, matchedResults);
        Collections.sort(matchedResults);
        return matchedResults;
    }

    private List<String> resolveCandidates(List<String> suggestions) {
        // ponytail: fast-path check avoiding collection allocation when no dynamic tokens are present
        boolean hasDynamicKey = false;
        for (String suggestion : suggestions) {
            if (suggestion.startsWith("$")) {
                hasDynamicKey = true;
                break;
            }
        }

        if (!hasDynamicKey) {
            return suggestions;
        }

        Set<String> resolvedValues = new HashSet<>();

        for (String suggestion : suggestions) {
            if (suggestion == null) {
                continue;
            }

            if (suggestion.startsWith("$")) {
                List<String> provided = suggestionRegistry.resolve(suggestion);
                if (provided != null) {
                    resolvedValues.addAll(provided);
                }
                continue;
            }
            resolvedValues.add(suggestion);
        }

        return List.copyOf(resolvedValues);
    }

    private record RuleEntry(ArgumentRule rule, String permission, List<String> suggestions) {
    }

    private record ArgumentRule(int position, List<String> path) {
        private ArgumentRule {
            path = path.stream().map(step -> step.toLowerCase(Locale.ROOT)).toList();
        }

        private boolean matches(String[] input) {
            if (input.length != position + 1) {
                return false;
            }

            if (path.isEmpty()) {
                return true;
            }

            // ponytail: match ancestor chain sequentially leading up to position
            int offset = position - path.size();
            for (int index = 0; index < path.size(); index++) {
                if (!input[offset + index].equalsIgnoreCase(path.get(index))) {
                    return false;
                }
            }

            return true;
        }
    }
}
