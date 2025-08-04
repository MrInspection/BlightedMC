package fr.moussax.blightedMC.commands.impl.testing;

import fr.moussax.blightedMC.core.items.ItemManager;
import fr.moussax.blightedMC.core.items.ItemsRegistry;
import fr.moussax.blightedMC.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TestCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if (!(label.equalsIgnoreCase("test") && sender instanceof Player player)) return false;

    ItemManager homodeusHelmet = ItemsRegistry.BLIGHTED_ITEMS.get("HOMODEUS_HELMET");
    ItemManager homodeusChestplate = ItemsRegistry.BLIGHTED_ITEMS.get("HOMODEUS_CHESTPLATE");
    ItemManager homodeusLeggings = ItemsRegistry.BLIGHTED_ITEMS.get("HOMODEUS_LEGGINGS");
    ItemManager homodeusBoots = ItemsRegistry.BLIGHTED_ITEMS.get("HOMODEUS_BOOTS");

    if (homodeusHelmet != null && homodeusChestplate != null && homodeusLeggings != null && homodeusBoots != null) {
      player.getInventory().addItem(
        homodeusHelmet.toItemStack(),
        homodeusChestplate.toItemStack(),
        homodeusLeggings.toItemStack(),
        homodeusBoots.toItemStack()
      );
      MessageUtils.informSender(player, "You received the §dHomodeus Armor §7.");
    } else {
      MessageUtils.warnSender(player, "Unable to find the Homodeus Armor in the registry.");
    }

    return true;
  }
}
