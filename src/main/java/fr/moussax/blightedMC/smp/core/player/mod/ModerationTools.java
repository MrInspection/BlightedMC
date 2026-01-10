package fr.moussax.blightedMC.smp.core.player.mod;

import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class ModerationTools {
    public static ItemStack getRandomTeleporter() {
        return new ItemBuilder(Material.ENDER_EYE)
            .setDisplayName("§dRandom Teleport §7(Right Click)")
            .addLore("§7Teleport to a random player.")
            .addEnchantmentGlint()
            .toItemStack();
    }

    public static ItemStack getInventoryInspector() {
        return new ItemBuilder(Material.CHEST)
            .setDisplayName("§dInspect Inventory §7(Right Click)")
            .addLore("§7View target player's inventory.")
            .toItemStack();
    }

    public static ItemStack getFreezer() {
        return new ItemBuilder(Material.PACKED_ICE)
            .setDisplayName("§dFreeze Target §7(Right Click)")
            .addLore("§7Freeze/Unfreeze a player.")
            .toItemStack();
    }

    public static ItemStack getKnockbackStick() {
        return new ItemBuilder(Material.STICK)
            .setDisplayName("§dAnti Knockback")
            .addLore("§7Test if target has antikb.")
            .addEnchantment(Enchantment.KNOCKBACK, 5)
            .toItemStack();
    }

    public static ItemStack getVanishTool(boolean isVanished) {
        return new ItemBuilder(isVanished ? Material.PURPLE_DYE : Material.GRAY_DYE)
            .setDisplayName(isVanished ? "§aBecome Visible §7(Right Click)" : "§cBecome Invisible §7(Right Click)")
            .addLore("§7Current status: " + (isVanished ? "§dVanished" : "§cVisible"))
            .toItemStack();
    }
}
