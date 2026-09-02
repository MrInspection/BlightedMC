package fr.moussax.blightedSMP.engine.items.rules;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Evaluates behavioral restriction rules for custom items.
 *
 * <p>An {@code ItemRuleEngine} maintains a collection of {@link ItemRule} instances and evaluates
 * whether actions such as block placement, player interaction, or generic event usage should be restricted.</p>
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
     * @param rule restriction rule to add
     */
    public void addRule(@NonNull ItemRule rule) {
        this.rules.add(rule);
    }

    /**
     * Evaluates whether block placement should be restricted for an item stack.
     *
     * @param event     block place event
     * @param itemStack item stack being placed
     * @return {@code true} if placement is restricted, {@code false} otherwise
     */
    public boolean shouldRestrictPlace(BlockPlaceEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictPlace(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Evaluates whether player interaction should be restricted for an item stack.
     *
     * @param event     player interact event
     * @param itemStack item stack being interacted with
     * @return {@code true} if interaction is restricted, {@code false} otherwise
     */
    public boolean shouldRestrictInteract(PlayerInteractEvent event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictInteract(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Evaluates whether generic usage should be restricted for an event and item stack.
     *
     * @param event     triggering event
     * @param itemStack item stack being used
     * @return {@code true} if usage is restricted, {@code false} otherwise
     */
    public boolean shouldRestrictUse(Event event, ItemStack itemStack) {
        for (ItemRule rule : rules) {
            if (rule.shouldRestrictUse(event, itemStack)) return true;
        }
        return false;
    }

    /**
     * Returns an unmodifiable list of all registered restriction rules.
     *
     * @return unmodifiable view of registered rules
     */
    public List<ItemRule> getRules() {
        return Collections.unmodifiableList(rules);
    }
}
