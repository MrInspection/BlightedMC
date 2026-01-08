package fr.moussax.blightedMC.smp.features.entities;

import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.smp.core.entities.spawnable.condition.SpawnRules;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;

public class Illusioner extends SpawnableEntity {

    public Illusioner() {
        super("ILLUSIONER", "Illusioner", 35, EntityType.ILLUSIONER, 0.05);
    }

    @Override
    protected void defineSpawnConditions() {
        addCondition(SpawnRules.insideStructure(Structure.MANSION));
    }
}
