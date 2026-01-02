package fr.moussax.blightedMC.utils.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class TabSuggestionBuilder implements TabCompleter {
    private final List<RuleEntry> rules = new ArrayList<>();

    public TabSuggestionBuilder(Class<?> commandClass) {
        var annotations = commandClass.getAnnotationsByType(CommandArgument.class);
        if (annotations.length == 0) {
            throw new IllegalArgumentException("Missing @CommandArgument annotation on " + commandClass.getSimpleName());
        }

        for (var argument : annotations) {
            rules.add(new RuleEntry(
                new ArgumentRule(argument.position(), argument.after()),
                List.of(argument.suggestions())
            ));
        }

        rules.sort((r1, r2) -> {
            boolean r1Specific = !r1.rule.after.isEmpty();
            boolean r2Specific = !r2.rule.after.isEmpty();
            return Boolean.compare(r2Specific, r1Specific);
        });
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        for (var entry : rules) {
            if (entry.rule.matches(args)) {
                List<String> candidates = getCandidates(entry.suggestions);

                List<String> result = new ArrayList<>();
                StringUtil.copyPartialMatches(args[args.length - 1], candidates, result);
                Collections.sort(result);
                return result;
            }
        }
        return Collections.emptyList();
    }

    private List<String> getCandidates(List<String> rawSuggestions) {
        List<String> result = new ArrayList<>();

        for (String suggestion : rawSuggestions) {
            if (TabSuggestionRegistry.contains(suggestion)) {
                result.addAll(TabSuggestionRegistry.resolve(suggestion));
            } else {
                result.add(suggestion);
            }
        }
        return result;
    }

    private record RuleEntry(ArgumentRule rule, List<String> suggestions) {
    }

    private static class ArgumentRule {
        private final int position;
        private final Set<String> after;

        ArgumentRule(int position, String[] after) {
            this.position = position;
            this.after = after.length == 0 ? Collections.emptySet() : Set.of(after);
        }

        boolean matches(String[] input) {
            if (input.length != position + 1) return false;
            if (position == 0) return true;
            return after.isEmpty() || after.contains(input[position - 1]);
        }
    }
}
