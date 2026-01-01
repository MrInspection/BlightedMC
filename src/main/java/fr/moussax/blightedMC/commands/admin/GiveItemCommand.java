package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.smp.core.items.registry.menu.ItemRegistryMenu;
import fr.moussax.blightedMC.utils.commands.CommandArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

@CommandArgument(suggestions = {"$items"})
public class GiveItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, String label, String @NonNull [] args) {
        if (!(label.equalsIgnoreCase("giveitem") && sender instanceof Player player)) return false;
        if (!enforceAdminPermission(player)) return false;

        if (args.length == 0) {
            BlightedMC.menuManager().openMenu(new ItemRegistryMenu.ItemCategoriesMenu(), player);
            return true;
        }

        String itemId = args[0].toUpperCase();
        BlightedItem blightedItem = ItemRegistry.getItem(itemId);

        int amount = 1;
        if (args.length > 1) {
            try {
                amount = Math.max(1, Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                warn(player, "Please provide a valid amount. Provided: §4" + args[1]);
                return false;
            }
        }

        ItemStack stack = blightedItem.toItemStack().clone();
        stack.setAmount(amount);
        player.getInventory().addItem(stack);
        inform(player, "You receive §5" + amount + "x §d" + blightedItem.getItemId() + "§7.");
        return true;
    }
}
