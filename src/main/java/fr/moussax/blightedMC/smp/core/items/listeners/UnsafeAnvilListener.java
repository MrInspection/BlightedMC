package fr.moussax.blightedMC.smp.core.items.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.Map;

public final class UnsafeAnvilListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack leftItem = inventory.getItem(0);
        ItemStack rightItem = inventory.getItem(1);

        if (leftItem == null || rightItem == null || rightItem.getType() != Material.ENCHANTED_BOOK) {
            return;
        }

        ItemMeta rightMeta = rightItem.getItemMeta();
        if (!(rightMeta instanceof EnchantmentStorageMeta bookMeta) || !bookMeta.hasStoredEnchants()) {
            return;
        }

        ItemStack result = leftItem.clone();
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta == null) return;

        boolean isResultBook = result.getType() == Material.ENCHANTED_BOOK;
        Map<Enchantment, Integer> currentEnchants = isResultBook && resultMeta instanceof EnchantmentStorageMeta storageMeta
            ? storageMeta.getStoredEnchants()
            : resultMeta.getEnchants();

        boolean changed = false;

        for (Map.Entry<Enchantment, Integer> entry : bookMeta.getStoredEnchants().entrySet()) {
            Enchantment enchant = entry.getKey();
            int bookLevel = entry.getValue();

            if (!enchant.canEnchantItem(result) && !isResultBook) continue;

            int currentLevel = currentEnchants.getOrDefault(enchant, 0);
            int finalLevel = Math.max(currentLevel, bookLevel);

            if (finalLevel == currentLevel) continue;

            if (isResultBook && resultMeta instanceof EnchantmentStorageMeta storageMeta) {
                storageMeta.addStoredEnchant(enchant, finalLevel, true);
            } else {
                resultMeta.addEnchant(enchant, finalLevel, true);
            }

            changed = true;
        }

        if (!changed) return;

        ItemMeta leftMeta = leftItem.getItemMeta();
        if (resultMeta instanceof Repairable resultRepairable && leftMeta instanceof Repairable leftRepairable) {
            int leftRepairCost = leftRepairable.getRepairCost();
            int rightRepairCost = rightMeta instanceof Repairable r ? r.getRepairCost() : 0;
            resultRepairable.setRepairCost(leftRepairCost + rightRepairCost + 5);
        }

        result.setItemMeta(resultMeta);
        event.setResult(result);
    }
}
