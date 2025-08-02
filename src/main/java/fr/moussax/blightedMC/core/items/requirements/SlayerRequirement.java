package fr.moussax.blightedMC.core.items.requirements;

import fr.moussax.blightedMC.core.players.BlightedPlayer;
import fr.moussax.blightedMC.core.slayers.Slayers;
import org.bukkit.inventory.ItemStack;

public class SlayerRequirement implements Requirement {
  private final Slayers slayer;
  private final int requiredLevel;

  public SlayerRequirement(Slayers slayer, int requiredLevel) {
    this.slayer = slayer;
    this.requiredLevel = requiredLevel;
  }

  @Override
  public boolean isAuthorized(BlightedPlayer player, ItemStack itemStack) {
    return true;
  }

  @Override
  public String requirementNotificationMessage() {
    return "§4☠ §cRequires §5" + slayer.getName() + " " + requiredLevel + ".";
  }
}
