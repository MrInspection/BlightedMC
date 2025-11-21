package fr.moussax.blightedMC.core.entities.loot.gems;

import fr.moussax.blightedMC.core.entities.loot.DroppableConsumable;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Location;

import java.util.Objects;

public record GemsLoot(int amount) implements DroppableConsumable {
    @Override
    public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer) {
        if (toPlayer) {
            killer.addGems(amount);
        } else {
            Objects.requireNonNull(dropLocation.getWorld()).dropItemNaturally(dropLocation, new GemsItem(amount).createItemStack());
        }
    }

    @Override
    public String name() {
        return "§5Blighted Gemstone";
    }
}
