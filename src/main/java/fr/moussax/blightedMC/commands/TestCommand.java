package fr.moussax.blightedMC.commands;

import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, String label, String @NonNull [] args) {
        if (!(label.equalsIgnoreCase("test") && sender instanceof Player player)) return false;

        ItemStack godSword = new ItemBuilder(Material.NETHERITE_SWORD)
                .setDisplayName("§dAspect of the Blight")
                .addEnchantment(Enchantment.SHARPNESS, 5)
                .addEnchantment(Enchantment.SMITE, 5)
                .addEnchantment(Enchantment.BANE_OF_ARTHROPODS, 5)
                .addEnchantment(Enchantment.LOOTING, 5)
                .unbreakable().toItemStack();

        ItemStack temp = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("&dAmethyst Cristal")
                .setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRmYzYyY2Y0Nzc3YWJmMzU2NWE3MDk4NTI3NzhlMjQ4YWFhZTkzNmZkNTE1N2MzMWRiMmEzYzI0NzBhNjY1YyJ9fX0=")
                .toItemStack();

        player.getInventory().addItem(godSword, temp);

        return true;
    }
}
