package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.core.entities.BlightedEntity;
import fr.moussax.blightedMC.core.entities.EntitiesRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class CommandTabSuggestionBuilder implements TabCompleter {
  public final HashMap<ArgumentRule, List<String>> rules = new LinkedHashMap<>();

  public CommandTabSuggestionBuilder(Class<?> commandClass) {
    var annotations = commandClass.getAnnotationsByType(CommandArgument.class);
    if (annotations.length == 0) {
      throw new IllegalArgumentException("Missing @CommandArgument annotation.");
    }

    for (var argument : annotations) {
        rules.put(new ArgumentRule(argument.position(), argument.after()), List.of(argument.suggestions()));
    }
  }

  @Override
  public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
    for (var entry : rules.entrySet()) {
      if (entry.getKey().matches(args)) {
        List<String> suggestions = entry.getValue();

        if (suggestions.size() == 1 && suggestions.getFirst().equals("$players")) {
          return Bukkit.getOnlinePlayers().stream()
              .map(Player::getName)
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

  private static class ArgumentRule {
    private final int position;
    private final Set<String> after;

    ArgumentRule(int position, String[] after) {
      this.position = position;
      this.after = Set.of(after);
    }

    boolean matches(String[] input) {
      if (input.length != position + 1) return false;
      return after.isEmpty() || after.contains(input[position - 1]);
    }
  }
}
