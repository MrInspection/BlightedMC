package fr.moussax.blightedMC.engine.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates rule registration and evaluation logic for custom items.
 *
 * // ponytail: simplified — encapsulates rule collection and evaluation into a deep module.
 */
public final class ItemRuleEngine {

    private final List<ItemRule> rules = new ArrayList<>();

    /**
     * Constructs an empty item rule engine.
     */
    public ItemRuleEngine() {
    }

    /**
     * Registers an item restriction rule.
     *
     * @param rule rule to register
     */
    public void addRule(@NonNull ItemRule rule) {
        this.rules.add(rule);
    }

    /**
     * Evaluates whether block placement should be restricted.
     *
     * @param event     block place event
     * @param itemStack item stack being placed
     * @return {@code true} if placement is restricted (cancel event), {@code false} otherwise
     */
    public boolean shouldRestrictPlace(BlockPlaceEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictPlace(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Evaluates whether player interaction should be restricted.
     *
     * @param event     player interact event
     * @param itemStack item stack being interacted with
     * @return {@code true} if interaction is restricted (cancel event), {@code false} otherwise
     */
    public boolean shouldRestrictInteract(PlayerInteractEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictInteract(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Evaluates whether generic usage should be restricted for an event.
     *
     * @param event     triggering event
     * @param itemStack item stack being used
     * @return {@code true} if usage is restricted (cancel event), {@code false} otherwise
     */
    public boolean shouldRestrictUse(Event event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictUse(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Returns an unmodifiable list of registered rules.
     *
     * @return registered rules
     */
    public List<ItemRule> getRules() {
        return Collections.unmodifiableList(rules);
    }
}
