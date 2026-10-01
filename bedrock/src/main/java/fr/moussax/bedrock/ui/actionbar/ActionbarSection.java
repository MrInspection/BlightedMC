package fr.moussax.bedrock.ui.actionbar;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Modular content section rendered within a player's action bar.
 *
 * <p>Standard sections render side-by-side joined by a separator, sorted by {@code priority}
 * ascending (lowest value rendered leftmost). Exclusive sections override all standard sections
 * whenever visible and returning non-empty text, sorted by {@code priority} descending (the highest value
 * takes precedence).</p>
 *
 * @param id           unique section identifier
 * @param priority     horizontal layout order for standard sections, or precedence weight for exclusive sections
 * @param textSupplier function producing display text for a player, or {@code null} if empty
 * @param visibility   predicate determining whether this section is visible for a player
 * @param exclusive    whether this section overrides standard sections when active
 * @param separator    custom delimiter rendered after this section, or {@code null} to use composer default
 */
public record ActionbarSection(
        @NonNull String id,
        int priority,
        @NonNull Function<Player, @Nullable String> textSupplier,
        @NonNull Predicate<Player> visibility,
        boolean exclusive,
        @Nullable String separator
) {

    /**
     * Constructs a standard or exclusive action bar section with default separator inheritance.
     *
     * @param id           unique section identifier
     * @param priority     horizontal rendering order or precedence
     * @param textSupplier function producing display text for a player
     * @param visibility   predicate controlling section display
     * @param exclusive    whether this section overrides standard sections
     */
    public ActionbarSection(
            @NonNull String id,
            int priority,
            @NonNull Function<Player, @Nullable String> textSupplier,
            @NonNull Predicate<Player> visibility,
            boolean exclusive
    ) {
        this(id, priority, textSupplier, visibility, exclusive, null);
    }

    /**
     * Constructs a standard (non-exclusive) action bar section.
     *
     * @param id           unique section identifier
     * @param priority     horizontal rendering order (lowest first)
     * @param textSupplier function producing display text for a player
     * @param visibility   predicate controlling section display
     */
    public ActionbarSection(
            @NonNull String id,
            int priority,
            @NonNull Function<Player, @Nullable String> textSupplier,
            @NonNull Predicate<Player> visibility
    ) {
        this(id, priority, textSupplier, visibility, false);
    }

    /**
     * Creates a standard action bar section that is always visible.
     *
     * @param id           unique section identifier
     * @param priority     horizontal rendering order (lowest first)
     * @param textSupplier function producing display text for a player
     * @return created the action bar section
     */
    @NonNull
    public static ActionbarSection of(
            @NonNull String id,
            int priority,
            @NonNull Function<Player, @Nullable String> textSupplier
    ) {
        return new ActionbarSection(id, priority, textSupplier, _ -> true, false);
    }

    /**
     * Creates a standard action bar section with custom visibility predicate.
     *
     * @param id           unique section identifier
     * @param priority     horizontal rendering order (lowest first)
     * @param textSupplier function producing display text for a player
     * @param visibility   predicate controlling section display
     * @return created the action bar section
     */
    @NonNull
    public static ActionbarSection of(
            @NonNull String id,
            int priority,
            @NonNull Function<Player, @Nullable String> textSupplier,
            @NonNull Predicate<Player> visibility
    ) {
        return new ActionbarSection(id, priority, textSupplier, visibility, false);
    }

    /**
     * Creates an exclusive action bar section that overrides standard sections when visible.
     *
     * @param id           unique section identifier
     * @param priority     override precedence (highest first)
     * @param textSupplier function producing display text for a player
     * @param visibility   predicate controlling section display
     * @return created an exclusive action bar section
     */
    @NonNull
    public static ActionbarSection exclusive(
            @NonNull String id,
            int priority,
            @NonNull Function<Player, @Nullable String> textSupplier,
            @NonNull Predicate<Player> visibility
    ) {
        return new ActionbarSection(id, priority, textSupplier, visibility, true);
    }

    /**
     * Starts building an action bar section with custom properties.
     *
     * @param id unique section identifier
     * @return new section builder
     */
    @NonNull
    public static Builder builder(@NonNull String id) {
        return new Builder(id);
    }

    /**
     * Fluent builder for configuring {@link ActionbarSection} instances.
     */
    public static final class Builder {
        private final String id;
        private int priority = 0;
        private Function<Player, @Nullable String> textSupplier = _ -> null;
        private Predicate<Player> visibility = _ -> true;
        private boolean exclusive = false;
        private String separator = null;

        private Builder(@NonNull String id) {
            this.id = Objects.requireNonNull(id, "id cannot be null");
        }

        /**
         * Sets layout priority or precedence weight.
         *
         * @param priority rendering order for standard sections, or precedence for exclusive sections
         * @return this builder
         */
        @NonNull
        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Sets horizontal rendering order for standard sections (lowest value rendered leftmost).
         *
         * @param order horizontal display order
         * @return this builder
         */
        @NonNull
        public Builder order(int order) {
            this.priority = order;
            return this;
        }

        /**
         * Sets a custom delimiter to render immediately after this section when joined with adjacent sections.
         * If {@code null} (the default), the composer or player's default separator is used.
         *
         * @param separator custom delimiter text rendered after this section, or {@code null} to use default
         * @return this builder
         */
        @NonNull
        public Builder separator(@Nullable String separator) {
            this.separator = separator;
            return this;
        }

        /**
         * Marks this section as exclusive with specified override precedence (highest value wins).
         *
         * @param precedence override precedence
         * @return this builder
         */
        @NonNull
        public Builder exclusive(int precedence) {
            this.exclusive = true;
            this.priority = precedence;
            return this;
        }

        /**
         * Marks this section as exclusive with default precedence (0).
         *
         * @return this builder
         */
        @NonNull
        public Builder exclusive() {
            this.exclusive = true;
            return this;
        }

        /**
         * Configures dynamic text generation for viewing players.
         *
         * @param textSupplier function returning formatted text or {@code null} if empty
         * @return this builder
         */
        @NonNull
        public Builder render(@NonNull Function<Player, @Nullable String> textSupplier) {
            this.textSupplier = Objects.requireNonNull(textSupplier, "textSupplier cannot be null");
            return this;
        }

        /**
         * Configures static constant text for this section.
         *
         * @param staticText constant text to display
         * @return this builder
         */
        @NonNull
        public Builder text(@NonNull String staticText) {
            Objects.requireNonNull(staticText, "staticText cannot be null");
            this.textSupplier = _ -> staticText;
            return this;
        }

        /**
         * Sets visibility condition controlling whether this section renders for a player.
         *
         * @param visibility predicate evaluating viewing player
         * @return this builder
         */
        @NonNull
        public Builder visibleWhen(@NonNull Predicate<Player> visibility) {
            this.visibility = Objects.requireNonNull(visibility, "visibility cannot be null");
            return this;
        }

        /**
         * Constructs the immutable {@link ActionbarSection}.
         *
         * @return configured action bar section
         */
        @NonNull
        public ActionbarSection build() {
            return new ActionbarSection(id, priority, textSupplier, visibility, exclusive, separator);
        }
    }
}
