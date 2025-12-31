package fr.moussax.blightedMC.smp.features.items.materials;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.common.PreventPlacementRule;
import org.bukkit.Material;

public class NetherMaterials implements ItemProvider {
    @Override
    public void register() {

        BlightedItem enchantedGhastTear = new BlightedItem("ENCHANTED_GHAST_TEAR", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.GHAST_TEAR);
        enchantedGhastTear.setDisplayName("Enchanted Ghast Tear");
        enchantedGhastTear.addLore(ItemRarity.UNCOMMON.getName());
        enchantedGhastTear.addEnchantmentGlint();

        BlightedItem enchantedMagmaCream = new BlightedItem("ENCHANTED_MAGMA_CREAM", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.MAGMA_CREAM);
        enchantedMagmaCream.setDisplayName("Enchanted Magma Cream");
        enchantedMagmaCream.addLore(ItemRarity.UNCOMMON.getName());
        enchantedMagmaCream.addEnchantmentGlint();

        BlightedItem enchantedQuartz = new BlightedItem("ENCHANTED_QUARTZ", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.QUARTZ);
        enchantedQuartz.setDisplayName("Enchanted Quartz");
        enchantedQuartz.addLore(ItemRarity.UNCOMMON.getName());
        enchantedQuartz.addEnchantmentGlint();

        BlightedItem enchantedBlazePowder = new BlightedItem("ENCHANTED_BLAZE_POWDER", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.BLAZE_POWDER);
        enchantedBlazePowder.setDisplayName("Enchanted Blaze Powder");
        enchantedBlazePowder.addLore(ItemRarity.UNCOMMON.getName());
        enchantedBlazePowder.addEnchantmentGlint();

        BlightedItem enchantedBlazeRod = new BlightedItem("ENCHANTED_BLAZE_ROD", ItemType.MATERIAL, ItemRarity.RARE, Material.BLAZE_ROD);
        enchantedBlazeRod.setDisplayName("Enchanted Blaze Rod");
        enchantedBlazeRod.addLore(ItemRarity.RARE.getName());
        enchantedBlazeRod.addEnchantmentGlint();

        BlightedItem enchantedGlowstoneDust = new BlightedItem("ENCHANTED_GLOWSTONE_DUST", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.GLOWSTONE_DUST);
        enchantedGlowstoneDust.setDisplayName("Enchanted Glowstone Dust");
        enchantedGlowstoneDust.addLore(ItemRarity.UNCOMMON.getName());
        enchantedGlowstoneDust.addEnchantmentGlint();

        BlightedItem enchantedNetherWart = new BlightedItem("ENCHANTED_NETHER_WART", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.NETHER_WART);
        enchantedNetherWart.setDisplayName("Enchanted Nether Wart");
        enchantedNetherWart.addLore(ItemRarity.UNCOMMON.getName());
        enchantedNetherWart.addEnchantmentGlint();

        BlightedItem flames = new BlightedItem("FLAMES", ItemType.MATERIAL, ItemRarity.RARE, Material.BLAZE_POWDER);
        flames.setDisplayName("Flames");
        flames.addLore("",
            " §7Dredged from the deepest magma, ",
            " §7these §6abyssal embers §7burn with",
            " §7a cold heat that defies nature.",
            "",
            ItemRarity.RARE.getName()
        );
        flames.addEnchantmentGlint();

        BlightedItem sulfur = new BlightedItem("SULFUR", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.GLOWSTONE_DUST);
        sulfur.setDisplayName("Sulfur");
        sulfur.addLore(
            "", " §7A pungent precipitate scraped ",
            " §7from Nether vents, used to",
            " §7catalyze volatile reactions.",
            "",
            ItemRarity.UNCOMMON.getName()
        );

        BlightedItem enchantedSulfur = new BlightedItem("ENCHANTED_SULFUR", ItemType.MATERIAL, ItemRarity.RARE, Material.GLOWSTONE_DUST);
        enchantedSulfur.setDisplayName("Enchanted Sulfur");
        enchantedSulfur.addLore(ItemRarity.RARE.getName());
        enchantedSulfur.addEnchantmentGlint();

        BlightedItem enchantedNetherrack = new BlightedItem("ENCHANTED_NETHERRACK", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.NETHERRACK);
        enchantedNetherrack.setDisplayName("Enchanted Netherrack");
        enchantedNetherrack.addLore(ItemRarity.UNCOMMON.getName());
        enchantedNetherrack.addEnchantmentGlint();

        // TODO - Finish items
        BlightedItem vengefulEye = new BlightedItem("VENGEFUL_EYE", ItemType.UNCATEGORIZED, ItemRarity.EPIC, Material.PLAYER_HEAD);
        vengefulEye.setDisplayName("Vengeful Eye");
        vengefulEye.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkzZWMyODVmNWM2NzcwZTdkZmMxZjI3NzVkMGU5NTYzOTk3Mzc4Njk5OWJkZDM2Y2M4ZTk5OGIyNWU3NTNmYiJ9fX0=");
        vengefulEye.addLore(ItemRarity.EPIC.getName());
        vengefulEye.addRule(new PreventPlacementRule());

        BlightedItem cremeBrulee = new BlightedItem("CREME_BRULEE", ItemType.UNCATEGORIZED, ItemRarity.UNCOMMON, Material.PUMPKIN_PIE);
        cremeBrulee.setDisplayName("Crème brûlée");
        cremeBrulee.addLore(
            "§8French Food",
            "",
            " §7Impervious to the flame that",
            " §7scorched its §6caramelized§7,",
            " §6crown§7, this custard remains",
            " §7velvety and untouched within. ",
            "",
            " §8“Sous le feu, la promesse",
            " §8d'un instant sucré.”",
            "",
            ItemRarity.UNCOMMON.getName() + " GOURMET"
        );
        cremeBrulee.editFood(food -> {
            food.setNutrition(10);
            food.setSaturation(14.4f);
            food.setCanAlwaysEat(true);
        });
        cremeBrulee.addEnchantmentGlint();

        BlightedItem suspiciousFungus = new BlightedItem("SUSPICIOUS_FUNGUS", ItemType.UNCATEGORIZED, ItemRarity.RARE, Material.CRIMSON_FUNGUS);
        suspiciousFungus.setDisplayName("SuSpIcIoUs Fungus");
        suspiciousFungus.addLore(ItemRarity.RARE.getName());
        suspiciousFungus.addRule(new PreventPlacementRule());
        suspiciousFungus.addEnchantmentGlint();

        add(
            enchantedGhastTear,
            enchantedMagmaCream,
            enchantedQuartz,
            enchantedBlazePowder,
            enchantedBlazeRod,
            enchantedGlowstoneDust,
            enchantedNetherWart,
            flames,
            sulfur,
            enchantedSulfur,
            enchantedNetherrack,
            vengefulEye,
            cremeBrulee,
            suspiciousFungus
        );
    }
}
