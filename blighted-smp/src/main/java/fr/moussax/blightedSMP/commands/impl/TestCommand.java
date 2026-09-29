package fr.moussax.blightedSMP.commands.impl;

import fr.moussax.bedrock.utils.ItemBuilder;
import fr.moussax.blightedSMP.commands.AdminCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public final class TestCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {

        ItemStack item = new ItemBuilder(Material.COPPER_HELMET)
                .setDisplayName("§fAngler Helmet")
                .addEnchantment(Enchantment.UNBREAKING, 5)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .addLore("",
                        " §8A simple garb from a simple ",
                        " §8craft. Though beneath the",
                        " §8surface, nothing is ever",
                        " §8quite so simple.",
                        "", "§f§lCOMMON"
                )
                .toItemStack();

        player.getInventory().addItem(item);
        return true;
    }
}
