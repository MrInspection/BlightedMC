package fr.moussax.blightedMC.core.entities;

import org.bukkit.entity.Entity;

public record EntityAttachment(Entity entity, BlightedEntity owner) { }
