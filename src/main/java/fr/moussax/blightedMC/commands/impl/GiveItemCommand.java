package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.commands.AdminCommand;
import fr.moussax.blightedMC.engine.items.BlightedItem;
import fr.moussax.blightedMC.engine.items.registry.ItemRegistry;
import fr.moussax.blightedMC.engine.items.registry.menu.ItemRegistryMenu;
import fr.moussax.blightedMC.commands.utils.CommandArgument;
import fr.moussax.blightedMC.commands.utils.CommandArguments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static fr.moussax.blightedMC.shared.text.Formatter.formatEnumName;
import static fr.moussax.blightedMC.shared.text.Messenger.inform;
import static fr.moussax.blightedMC.shared.text.Messenger.warn;

@CommandArguments({
        @CommandArgument(position = 0, suggestions = {"$players"}),
        @CommandArgument(position = 1, suggestions = {"$items"})
})
public final class GiveItemCommand extends AdminCommand {

    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {

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

        Map<Integer, ItemStack> overflow = target.getInventory().addItem(stack);
        for (ItemStack excess : overflow.values()) {
            target.getWorld().dropItem(target.getLocation(), excess);
        }

        inform(player, " §eGave §7x" + amount + " §f" + formatEnumName(blightedItem.getItemId()) + " §eto §d" + target.getName() + "§7.");

        return true;
    }
}
