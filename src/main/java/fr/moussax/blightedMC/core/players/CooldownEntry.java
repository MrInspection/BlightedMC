package fr.moussax.blightedMC.core.players;

import fr.moussax.blightedMC.core.items.abilities.AbilityManager;
import fr.moussax.blightedMC.core.items.abilities.AbilityType;

public record CooldownEntry(Class<? extends AbilityManager> abilityManager, AbilityType abilityType) {}
