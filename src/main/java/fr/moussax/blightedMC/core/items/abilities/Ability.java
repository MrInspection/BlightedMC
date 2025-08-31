package fr.moussax.blightedMC.core.items.abilities;

import org.bukkit.event.Event;

/**
 * Represents a gameplay ability with a manager, name, and type.
 *
 * <p>The {@code manager} handles event logic, {@code name} uniquely identifies
 * the ability, and {@code type} defines its behavior.</p>
 *
 * @param manager the event manager for this ability
 * @param name    the unique identifier
 * @param type    the ability's behavior type
 */
public record Ability(AbilityManager<? extends Event> manager, String name, AbilityType type) { }
