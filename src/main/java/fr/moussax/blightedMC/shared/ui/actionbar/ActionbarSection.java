package fr.moussax.blightedMC.shared.ui.actionbar;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a modular content section rendered within a player's action bar.
 *
 * @param id           unique identifier for this section
 * @param priority     rendering priority; lower values render further to the left
 * @param textSupplier function producing the display text for a player, or {@code null} if empty
 * @param visibility   predicate determining whether this section should render for a player
 */
public record ActionbarSection(
        @NonNull String id,
        int priority,
        @NonNull Function<Player, @Nullable String> textSupplier,
        @NonNull Predicate<Player> visibility
) {
    /**
     * Creates an action bar section that is always visible for all players.
     *
     * @param id           unique section identifier
     * @param priority     rendering priority
     * @param textSupplier text supplier function
     * @return created action bar section
     */
    public static ActionbarSection of(String id, int priority, Function<Player, @Nullable String> textSupplier) {
        return new ActionbarSection(id, priority, textSupplier, player -> true);
    }

    /**
     * Creates an action bar section with custom visibility predicate.
     *
     * @param id           unique section identifier
     * @param priority     rendering priority
     * @param textSupplier text supplier function
     * @param visibility   predicate controlling section display
     * @return created action bar section
     */
    public static ActionbarSection of(String id, int priority, Function<Player, @Nullable String> textSupplier, Predicate<Player> visibility) {
        return new ActionbarSection(id, priority, textSupplier, visibility);
    }
}
