package fr.moussax.blightedMC.commands.impl.testing;

import fr.moussax.blightedMC.core.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import java.util.List;

public class TestCommand implements CommandExecutor {
  @Override
  public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
    if(!(label.equalsIgnoreCase("test") && sender instanceof Player player)) return false;

    ItemBuilder stormChestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE)
        .setDisplayName("§dStorm Chestplate")
        .addEnchantmentGlint(true)
        .setUnbreakable(true)
        .setArmorTrim(TrimMaterial.LAPIS, TrimPattern.FLOW)
        .setLeatherColor("#1793C4");

    Pattern creeperPatter = new Pattern(DyeColor.YELLOW, PatternType.GLOBE);

    ItemBuilder banner = new ItemBuilder(Material.WHITE_BANNER)
        .addBannerPatterns(List.of(creeperPatter))
        .setDisplayName("§dGlobe Banner");

    ItemBuilder infernoHelmet = new ItemBuilder(Material.PLAYER_HEAD)
        .setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWU4Y2I0NTYxNGU4M2MxYmRmYzIzZjQ1MzY0ZjY1MzNhNDQ2YTEzYjgxMmQ4MzAzODY2OWViYzEwMzA1ZTNiNSJ9fX0=")
        .setDisplayName("§dHellFire Inferno Helmet")
        .addItemFlag(List.of(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS))
        .addEnchantment(Enchantment.PROTECTION, 5)
        .addEnchantment(Enchantment.FIRE_PROTECTION, 5)
        .addLore(
            "§8Forged Equipment", " ",
            "§7 Heat Tier: §c\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25 §8(HellFire)", " ",
            "§5 Full Set Bonus: Hellfire Wreath §8(0/4)",
            "§7 Enemies within §d5 §7blocks takes §d+20% \uD83D\uDD25 damage§7. ",
            "§7 Emits a §cburning aura §7that deals §d10 §7true ",
            "§7 damage per second to nearby foes.", " ",
            "§7 Immune to fire and lava.",
            "§7 Grants §e+15% ⚔ attack speed §7while on fire.", " ",
            "§d§lMYTHIC HELMET"
        );

    ItemBuilder burningInfernoHelmet = new ItemBuilder(Material.NETHERITE_HELMET,"§6Burning Inferno Helmet")
        .addEnchantment(Enchantment.PROTECTION, 5)
        .setArmorTrim(TrimMaterial.RESIN, TrimPattern.FLOW)
        .addItemFlag(List.of(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ARMOR_TRIM))
        .addLore(
            "§8Forged Equipment", " ",
            "§7 Heat Tier: §c\uD83D\uDD25\uD83D\uDD25§8\uD83D\uDD25\uD83D\uDD25 §8(Burning)", " ",
            "§5 Full Set Bonus: Burning Wreath §8(0/4)",
            "§7 Enemies within §d5 §7blocks takes §d+10% \uD83D\uDD25 damage§7. ",
            "§7 Emits a §cburning aura §7that deals §d5 §7true ",
            "§7 damage per second to nearby foes.", " ",
            "§7 Immune to fire and lava.",
            "§7 Grants §e+5% ⚔ attack speed §7while on fire.", " ",
            "§6§lLEGENDARY HELMET"
        );

    player.getInventory().addItem(stormChestplate.toItemStack(), banner.toItemStack(), infernoHelmet.toItemStack(), burningInfernoHelmet.toItemStack());
    player.sendMessage("§7You received items");

    return true;
  }
}
