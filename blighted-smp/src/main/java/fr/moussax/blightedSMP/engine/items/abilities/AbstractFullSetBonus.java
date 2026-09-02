package fr.moussax.blightedSMP.engine.items.abilities;

import fr.moussax.blightedSMP.engine.player.BlightedPlayer;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract base implementation of {@link FullSetBonus} providing standard player binding
 * and piece tracking boilerplate.
 *
 * <p>Subclasses only need to provide {@link #getName()}, {@link #getDescription()},
 * {@link #startAbilityEffect()}, and {@link #stopAbilityEffect()}.</p>
 */
public abstract class AbstractFullSetBonus implements FullSetBonus {

    @Getter
    private final int maxPieces;
    @Getter
    @Setter
    private int pieces;
    @Getter
    private BlightedPlayer abilityOwner;

    /**
     * Creates a full set bonus requiring 4 armor pieces by default.
     */
    protected AbstractFullSetBonus() {
        this(4);
    }

    /**
     * Creates a set bonus requiring the specified number of armor pieces.
     *
     * @param maxPieces required armor piece count
     */
    protected AbstractFullSetBonus(int maxPieces) {
        this.maxPieces = maxPieces;
        this.pieces = maxPieces;
    }

    @Override
    public void setPlayer(BlightedPlayer player) {
        this.abilityOwner = player;
    }
}
