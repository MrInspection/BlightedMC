package fr.moussax.blightedMC.commands.admin;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.server.PluginPermissions;
import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.registry.ItemRegistry;
import fr.moussax.blightedMC.smp.core.items.registry.menu.ItemRegistryMenu;
import fr.moussax.blightedMC.utils.commands.CommandArgument;
import fr.moussax.blightedMC.utils.commands.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import static fr.moussax.blightedMC.utils.formatting.Formatter.*;

@CommandArguments({
    @CommandArgument(suggestions = {"$players"}),
    @CommandArgument(position = 1, suggestions = {"$items"})
})
public class GiveItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) return false;
        if (!hasRequiredPermission(player, PluginPermissions.ADMIN)) return false;

        if (args.length == 0) {
            BlightedMC.menuManager().openMenu(new ItemRegistryMenu.ItemCategoriesMenu(), player);
            return true;
        }

        Player target = player;
        int argumentIndex = 0;

        Player potentialTarget = Bukkit.getPlayerExact(args[0]);
        if (potentialTarget != null) {
            target = potentialTarget;
            argumentIndex = 1;
        }

        if (args.length < argumentIndex + 1) {
            warn(player, "Missing argument. Please provide an item Id.");
            return false;
        }

        String itemId = args[argumentIndex].toUpperCase();
        BlightedItem blightedItem = ItemRegistry.getItem(itemId);

        int amount = 1;
        if (args.length > argumentIndex + 1) {
            try {
                amount = Math.max(1, Integer.parseInt(args[argumentIndex + 1]));
            } catch (NumberFormatException e) {
                warn(player, "Please provide a valid amount. Provided: §4" + args[argumentIndex + 1]);
                return false;
            }
        }

        ItemStack stack = blightedItem.toItemStack().clone();
        stack.setAmount(amount);
        target.getInventory().addItem(stack);

        inform(player, "Gave §ex" + amount + " §f" + formatEnumName(blightedItem.getItemId()) + " §7to §e" + target.getName() + "§7.");
        return true;
    }
}
