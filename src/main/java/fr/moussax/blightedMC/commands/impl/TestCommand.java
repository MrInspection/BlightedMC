package fr.moussax.blightedMC.commands.impl;

import fr.moussax.blightedMC.commands.AdminCommand;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class TestCommand extends AdminCommand {
    @Override
    protected boolean executeAdmin(Player player, Command command, String label, String[] args) {
        player.sendMessage(" §eGave §d25 §egems to §fTrixmas§e.");
        player.sendMessage(" §eYou gave all players §d25 §egems.");
        player.sendMessage(" §eYou reset all players' gems balance.");
        player.sendMessage("");
        player.sendMessage(" §7You received §d25 §7gems.");
        player.sendMessage(" §7Your gems balance has been §creset§e.");
        player.sendMessage(" §7Your gems balance was set to §d67 §7gems.");
        player.sendMessage("");


        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 100, true);
        itemStack.setItemMeta(meta);

        player.getInventory().addItem(itemStack);



        return true;
    }
}
