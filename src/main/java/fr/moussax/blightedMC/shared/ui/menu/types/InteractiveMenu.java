package fr.moussax.blightedMC.shared.ui.menu.types;

import fr.moussax.blightedMC.shared.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for menus that allow player interaction with designated slots.
 *
 * <p>Interactable slots allow players to place and remove items. Subclasses
 * are notified of changes through {@link #onUpdate(Player)}.</p>
 */
public abstract class InteractiveMenu extends Menu {

    private final Set<Integer> interactableSlots = new HashSet<>();

    /**
     * Creates an interactive menu.
     *
     * @param title menu title
     * @param size  inventory size (multiple of 9)
     */
    public InteractiveMenu(String title, int size) {
        super(title, size);
    }

    /**
     * Marks the specified slots as interactable.
     *
     * <p>Interactable slots allow players to place and remove items.</p>
     *
     * @param slots slot indices to make interactable
     */
    protected void addInteractableSlots(int... slots) {
        for (int slot : slots) {
            interactableSlots.add(slot);
        }
    }

    /**
     * Checks whether the specified slot allows player interaction.
     *
     * @param slot slot index
     * @return {@code true} if the slot is interactable
     */
    @Override
    public boolean isInteractable(int slot) {
        return interactableSlots.contains(slot);
    }

    /**
     * Handles changes to the menu's interactable slots.
     *
     * <p>Called after a player places, removes, or otherwise changes an item
     * in an interactable slot.</p>
     *
     * @param player player whose interaction changed the menu
     */
    @Override
    public abstract void onUpdate(@NonNull Player player);
}
