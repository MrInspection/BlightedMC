package fr.moussax.blightedMC.content.rituals;

import fr.moussax.blightedMC.content.entities.Illusioner;
import fr.moussax.blightedMC.engine.entities.rituals.AncientRitual;
import fr.moussax.blightedMC.registry.RegistryModule;
import fr.moussax.blightedMC.engine.entities.rituals.registry.RitualRegistryHandler;
import org.bukkit.Material;

public class AncientRituals implements RegistryModule<RitualRegistryHandler> {

    @Override
    public void register(RitualRegistryHandler registry) {

        AncientRitual dummy = AncientRitual.Builder.of(new Illusioner())
                .displayedItem(Material.ENDER_PEARL, builder -> builder.setDisplayName("hello"))
                .addOffering(Material.DIRT, 45)
                .addOffering(Material.EMERALD, 45)
                .gemsCost(12)
                .levelCost(3)
                .build();

        registry.register(dummy);
    }
}
