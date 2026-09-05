package fr.moussax.bedrock.ui.actionbar;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a modular content section rendered within a player's action bar.
 *
 * <p>For normal (non-exclusive) sections, priority determines left-to-right rendering position
 * (lower values render further to the left). For exclusive sections, priority defines override precedence
 * (higher values take precedence over lower priority exclusive sections when multiple exclusive sections produce non-empty text).</p>
 *
 * @param id           unique identifier for this section
 * @param priority     rendering priority for normal sections or precedence for exclusive sections
 * @param textSupplier function producing the display text for a player, or {@code null} if empty
 * @param visibility   predicate determining whether this section should render for a player
 * @param exclusive    whether this section overrides non-exclusive sections when visible with non-empty text
 */
public record ActionbarSection(
        @NonNull String id,
        int priority,
        @NonNull Function<Player, @Nullable String> textSupplier,
        @NonNull Predicate<Player> visibility,
        boolean exclusive
) {
    public ActionbarSection(
            @NonNull String id,
            int priority,
            @NonNull Function<Player, @Nullable String> textSupplier,
            @NonNull Predicate<Player> visibility
    ) {
        this(id, priority, textSupplier, visibility, false);
    }
    /**
     * Creates an action bar section that is always visible for all players.
     *
     * @param id           unique section identifier
     * @param priority     rendering priority
     * @param textSupplier text supplier function
     * @return created action bar section
     */
    public static ActionbarSection of(String id, int priority, Function<Player, @Nullable String> textSupplier) {
        return new ActionbarSection(id, priority, textSupplier, player -> true, false);
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
        return new ActionbarSection(id, priority, textSupplier, visibility, false);
    }

    /**
     * Creates an exclusive action bar section that overrides non-exclusive sections when visible.
     *
     * @param id           unique section identifier
     * @param priority     rendering priority
     * @param textSupplier text supplier function
     * @param visibility   predicate controlling section display
     * @return created exclusive action bar section
     */
    public static ActionbarSection exclusiveOf(String id, int priority, Function<Player, @Nullable String> textSupplier, Predicate<Player> visibility) {
        return new ActionbarSection(id, priority, textSupplier, visibility, true);
    }
}
