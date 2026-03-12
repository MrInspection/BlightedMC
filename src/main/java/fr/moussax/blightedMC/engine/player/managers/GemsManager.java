package fr.moussax.blightedMC.engine.player.managers;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GemsManager {
    private int gems = 0;

    public void addGems(int value) {
        gems += value;
    }

    public void removeGems(int value) {
        gems = Math.max(0, gems - value);
    }

    public boolean hasEnoughGems(int value) {
        return gems >= value;
    }

}
