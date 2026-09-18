package fr.moussax.blightedMod.moderator.punishments;

import java.util.UUID;

/**
 * Immutable identity record for resolving online and offline player target details.
 *
 * @param uniqueId  player UUID
 * @param name      player name
 * @param ipAddress player last known IP address
 */
public record OfflineTargetIdentity(UUID uniqueId, String name, String ipAddress) {}
