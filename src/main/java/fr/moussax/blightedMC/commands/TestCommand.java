package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.utils.ColorUtils;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, String label, String @NonNull [] args) {
        if (!(label.equalsIgnoreCase("test") && sender instanceof Player player)) return false;

        ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
                .setDisplayName("&#FF5555Excalibur")
                .addLore("&#A800FFRarity: Mythic")
                .addLore("&#00FFD4Damage: +15")
                .toItemStack();
        player.getInventory().addItem(item);
        player.sendMessage(ColorUtils.colorize("&#2596be Your item has been added to your inventory!"));
        player.sendMessage(ColorUtils.colorize("&#d62524 You must be ADMIN to use this command."));
        player.sendMessage("§4 You must be ADMIN to use this command.");


        return true;
    }
}
