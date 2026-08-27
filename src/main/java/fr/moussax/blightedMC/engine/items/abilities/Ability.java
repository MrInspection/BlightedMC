package fr.moussax.blightedMC.engine.items.abilities;

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

    /**
     * Creates an ability with the specified name, trigger type, and execution manager.
     *
     * @param name    ability display name
     * @param type    trigger type
     * @param manager execution manager
     * @return a new ability instance
     */
    public static Ability of(String name, AbilityType type, AbilityManager<? extends Event> manager) {
        return new Ability(manager, name, type);
    }

    /**
     * Creates a right-click triggered ability.
     *
     * @param name    ability display name
     * @param manager execution manager
     * @return a new right-click ability instance
     */
    public static Ability rightClick(String name, AbilityManager<? extends Event> manager) {
        return of(name, AbilityType.RIGHT_CLICK, manager);
    }

    /**
     * Creates a left-click triggered ability.
     *
     * @param name    ability display name
     * @param manager execution manager
     * @return a new left-click ability instance
     */
    public static Ability leftClick(String name, AbilityManager<? extends Event> manager) {
        return of(name, AbilityType.LEFT_CLICK, manager);
    }

    /**
     * Creates a passive ability.
     *
     * @param name    ability display name
     * @param manager execution manager
     * @return a new passive ability instance
     */
    public static Ability passive(String name, AbilityManager<? extends Event> manager) {
        return of(name, AbilityType.PASSIVE, manager);
    }

    /**
     * Creates a sneak triggered ability.
     *
     * @param name    ability display name
     * @param manager execution manager
     * @return a new sneak ability instance
     */
    public static Ability sneak(String name, AbilityManager<? extends Event> manager) {
        return of(name, AbilityType.SNEAK, manager);
    }

    /**
     * Formats and constructs lore lines displaying the ability name, type, description, and resource costs.
     *
     * @return formatted list of item lore lines
     */
    public List<String> getAbilityLore() {
        List<String> lore = new ArrayList<>();

        lore.add("");
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
