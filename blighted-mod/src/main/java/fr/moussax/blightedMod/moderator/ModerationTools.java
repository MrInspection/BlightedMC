package fr.moussax.blightedMod.moderator;

import fr.moussax.bedrock.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public final class ModerationTools {
    public static ItemStack getRandomTeleporter() {
        return new ItemBuilder(Material.ENDER_EYE)
                .setDisplayName("§dRandom Teleport §7(Right Click)")
                .addLore("§7Teleport to a random player.")
                .addEnchantmentGlint()
                .toItemStack();
    }

    public static ItemStack getReportViewer() {
        return new ItemBuilder(Material.BOOK)
                .setDisplayName("§dReport Center §7(Right Click)")
                .addLore("§7View and manage player reports.")
                .toItemStack();
    }

    public static ItemStack getSanctionsInspector() {
        return new ItemBuilder(Material.ENCHANTED_BOOK)
                .setDisplayName("§dSanctions Inspector §7(Right Click)")
                .addLore("§7View target player's sanctions history.")
                .setEnchantmentGlint(false)
                .toItemStack();
    }

    public static ItemStack getKnockbackStick() {
        return new ItemBuilder(Material.STICK)
                .setDisplayName("§dAnti Knockback")
                .addLore("§7Test if target has Anti-KB Cheat.")
                .addEnchantment(Enchantment.KNOCKBACK, 4)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .toItemStack();
    }

    public static ItemStack getFreezer() {
        return new ItemBuilder(Material.PACKED_ICE)
                .setDisplayName("§dFreeze Target §7(Right Click)")
                .addLore("§7Prevent the target from moving.")
                .toItemStack();
    }

    public static ItemStack getInventoryInspector() {
        return new ItemBuilder(Material.CHEST)
                .setDisplayName("§dInspect Inventory §7(Right Click)")
                .addLore("§7View target inventory's inventory contents.")
                .toItemStack();
    }

    public static ItemStack getVanishTool(boolean isVanished) {
        return new ItemBuilder(isVanished ? Material.PURPLE_DYE : Material.GRAY_DYE)
                .setDisplayName(isVanished ? "§aBecome Visible §7(Right Click)" : "§cBecome Invisible §7(Right Click)")
                .addLore("§7Current status: " + (isVanished ? "§dVanished" : "§cVisible"))
                .toItemStack();
    }
}