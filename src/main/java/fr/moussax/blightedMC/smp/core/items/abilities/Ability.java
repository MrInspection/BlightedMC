package fr.moussax.blightedMC.smp.core.items.abilities;

import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

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
public record Ability(AbilityManager<? extends Event> manager, String name, AbilityType type) {
    public List<String> getAbilityLore() {
        List<String> lore = new ArrayList<>();

        lore.add("§5 Ability: " + name + "  " + type.getDisplayName());

        for (String line : manager.getDescription()) {
            lore.add("§7 " + line);
        }

        int mana = manager.getManaCost();
        int cooldown = manager.getCooldownSeconds();

        if (mana > 0) {
            lore.add("§8 Mana Cost: §3" + mana);
        }
        if (cooldown > 0) {
            lore.add("§8 Cooldown: §a" + cooldown + "s");
        }
        return lore;
    }
}
