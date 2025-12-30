package fr.moussax.blightedMC.smp.core.shared.ui.menu;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

public abstract class InteractiveMenu extends Menu {
    private final Set<Integer> interactableSlots = new HashSet<>();

    public InteractiveMenu(String title, int size) {
        super(title, size);
    }

    /**
     * Marks specific slots as interactable, allowing players to place/remove items.
     *
     * @param slots the slot indices to unlock
     */
    protected void addInteractableSlots(int... slots) {
        for (int slot : slots) {
            interactableSlots.add(slot);
        }
    }

    /**
     * Checks if a slot is interactable.
     *
     * @param slot the slot index
     * @return true if the slot is unlocked
     */
    public boolean isInteractable(int slot) {
        return interactableSlots.contains(slot);
    }

    /**
     * Triggered when the inventory state changes (click/drag in interactable slots).
     * Useful for recalculating recipes or updating UI.
     *
     * @param player the player interacting
     */
    public abstract void onUpdate(@NonNull Player player);
}
