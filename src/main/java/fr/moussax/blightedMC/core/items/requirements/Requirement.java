package fr.moussax.blightedMC.core.items.requirements;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import org.bukkit.inventory.ItemStack;

public interface Requirement {
  boolean isAuthorized(BlightedPlayer player, ItemStack itemStack);
  default String requirementNotificationMessage() { return ""; }
}
