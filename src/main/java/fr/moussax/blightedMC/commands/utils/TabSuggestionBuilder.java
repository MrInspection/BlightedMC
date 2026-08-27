package fr.moussax.blightedMC.commands.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Produces positional tab completions for commands from {@link CommandArgument}
 * declarations.
 */
public final class TabSuggestionBuilder implements TabCompleter {

    private final TabSuggestionRegistry suggestionRegistry;
    private final List<RuleEntry> rules;

    /**
     * Creates a tab suggestion builder for a command class using the specified suggestion registry.
     *
     * @param commandType        command executor class declaring {@link CommandArgument} annotations
     * @param suggestionRegistry registry used to resolve dynamic suggestion keys
     */
    public TabSuggestionBuilder(Class<?> commandType, TabSuggestionRegistry suggestionRegistry) {
        Objects.requireNonNull(commandType, "commandType");
        this.suggestionRegistry = Objects.requireNonNull(suggestionRegistry, "suggestionRegistry");

        CommandArgument[] arguments = commandType.getAnnotationsByType(CommandArgument.class);

        if (arguments.length == 0) {
            throw new IllegalArgumentException(
                    "Command type '%s' has no @CommandArgument declarations".
                            formatted(commandType.getName()));
        }

        List<RuleEntry> configuredRules = new ArrayList<>(arguments.length);

        for (CommandArgument argument : arguments) {
            if (argument.position() < 0) {
                throw new IllegalArgumentException(
                        "Command argument position cannot be negative: "
                                + commandType.getName()
                );
            }

            configuredRules.add(
                    new RuleEntry(
                            new ArgumentRule(
                                    argument.position(),
                                    Set.of(argument.after())
                            ),
                            List.of(argument.suggestions())
                    )
            );
        }

        configuredRules.sort(
                Comparator
                        .comparingInt(
                                (RuleEntry entry) ->
                                        entry.rule().after().isEmpty() ? 1 : 0
                        )
                        .thenComparingInt(
                                entry -> entry.rule().position()
                        )
        );

        rules = List.copyOf(configuredRules);
    }

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

        for (RuleEntry entry : rules) {
            if (!entry.rule().matches(args)) {
                continue;
            }

            String currentInput = args[args.length - 1];
            List<String> candidates = resolveCandidates(entry.suggestions());
            List<String> matches = new ArrayList<>();

            StringUtil.copyPartialMatches(
                    currentInput,
                    candidates,
                    matches
            );

            return matches;
        }

        return List.of();
    }

    private List<String> resolveCandidates(List<String> suggestions) {
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

        Set<String> candidates = new HashSet<>();

        for (String suggestion : suggestions) {
            List<String> provided = suggestionRegistry.resolve(suggestion);

            if (provided == null) {
                candidates.add(suggestion);
            } else {
                candidates.addAll(provided);
            }
        }

        return List.copyOf(candidates);
    }

    private record RuleEntry(ArgumentRule rule, List<String> suggestions) {
    }

    private record ArgumentRule(int position, Set<String> after) {
        private ArgumentRule {
            after = after.stream()
                    .map(value -> value.toLowerCase(Locale.ROOT))
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
        }

        private boolean matches(String[] input) {
            if (input.length != position + 1) {
                return false;
            }

            if (position == 0 || after.isEmpty()) {
                return true;
            }

            return after.contains(
                    input[position - 1].toLowerCase(Locale.ROOT)
            );
        }
    }
}
