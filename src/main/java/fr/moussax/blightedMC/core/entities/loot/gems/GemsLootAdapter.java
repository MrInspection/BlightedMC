package fr.moussax.blightedMC.core.entities.loot.gems;

import fr.moussax.blightedMC.core.entities.loot.ItemLoot;
import fr.moussax.blightedMC.core.player.BlightedPlayer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class GemsLootAdapter extends ItemLoot {
    private final GemsLoot gemsLoot;

    public GemsLootAdapter(GemsLoot gems, ItemStack displayItem) {
        super(displayItem, gems.amount(), gems.amount());
        this.gemsLoot = gems;
    }

    @Override
    public int generateAmount() {
        return 1;
    }

    @Override
    public void consume(BlightedPlayer killer, Location dropLocation, boolean toPlayer, int amount) {
        gemsLoot.consume(killer, dropLocation, toPlayer);
    }

    @Override
    public String name() {
        return gemsLoot.name();
    }
}
