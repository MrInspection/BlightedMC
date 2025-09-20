package fr.moussax.blightedMC.core.fishing.LootTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class LootPool {
  private final List<LootEntry> entries = new ArrayList<>();
  private LootCondition globalCondition = LootCondition.alwaysTrue();

  public LootPool addWithCondition(LootEntry entry) {
    entries.add(entry);
    return this;
  }

  public LootPool addGlobalCondition(LootCondition condition) {
    this.globalCondition = condition;
    return this;
  }

  public Optional<LootEntry> roll(Random rand, LootContext ctx) {
    if (!globalCondition.test(ctx)) return Optional.empty();

    // Only valid entries for this context
    List<LootEntry> valid = entries.stream()
      .filter(e -> e.isValid(ctx))
      .toList();

    if (valid.isEmpty()) return Optional.empty();

    double totalWeight = valid.stream().mapToDouble(LootEntry::getWeight).sum();
    double roll = rand.nextDouble() * totalWeight;
    double cumulative = 0;

    for (LootEntry entry : valid) {
      cumulative += entry.getWeight();
      if (roll <= cumulative) return Optional.of(entry);
    }

    return Optional.of(valid.getLast()); // fallback
  }
}
