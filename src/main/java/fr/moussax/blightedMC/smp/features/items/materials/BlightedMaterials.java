package fr.moussax.blightedMC.smp.features.items.materials;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import org.bukkit.Material;

public class BlightedMaterials implements ItemProvider {

    @Override
    public void register() {
        BlightedItem enchantedIronIngot = new BlightedItem("ENCHANTED_IRON_INGOT", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.IRON_INGOT);
        enchantedIronIngot.setDisplayName("Enchanted Iron Ingot");
        enchantedIronIngot.addLore(ItemRarity.UNCOMMON.getName());
        enchantedIronIngot.addEnchantmentGlint();

        BlightedItem enchantedIronBlock = new BlightedItem("ENCHANTED_IRON_BLOCK", ItemType.MATERIAL, ItemRarity.RARE, Material.IRON_BLOCK);
        enchantedIronBlock.setDisplayName("Enchanted Iron Block");
        enchantedIronBlock.addEnchantmentGlint();
        enchantedIronBlock.addLore(ItemRarity.RARE.getName());
        enchantedIronBlock.addRule(ItemRule.PREVENT_PLACEMENT);

        BlightedItem enchantedCopperIngot = new BlightedItem("ENCHANTED_COPPER_INGOT", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.COPPER_INGOT);
        enchantedCopperIngot.setDisplayName("Enchanted Copper Ingot");
        enchantedCopperIngot.addLore(ItemRarity.UNCOMMON.getName());
        enchantedCopperIngot.addEnchantmentGlint();

        BlightedItem enchantedGoldIngot = new BlightedItem("ENCHANTED_GOLD_INGOT", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.GOLD_INGOT);
        enchantedGoldIngot.setDisplayName("Enchanted Gold Ingot");
        enchantedGoldIngot.addLore(ItemRarity.UNCOMMON.getName());
        enchantedGoldIngot.addEnchantmentGlint();

        BlightedItem enchantedLapisLazuli = new BlightedItem("ENCHANTED_LAPIS_LAZULI", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.LAPIS_LAZULI);
        enchantedLapisLazuli.setDisplayName("Enchanted Lapis Lazuli");
        enchantedLapisLazuli.addLore(ItemRarity.UNCOMMON.getName());
        enchantedLapisLazuli.addEnchantmentGlint();

        BlightedItem enchantedRedstone = new BlightedItem("ENCHANTED_REDSTONE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.REDSTONE);
        enchantedRedstone.setDisplayName("Enchanted Redstone");
        enchantedRedstone.addLore(ItemRarity.UNCOMMON.getName());
        enchantedRedstone.addEnchantmentGlint();

        BlightedItem enchantedAmethystShard = new BlightedItem("ENCHANTED_AMETHYST_SHARD", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.AMETHYST_SHARD);
        enchantedAmethystShard.setDisplayName("Enchanted Amethyst Shard");
        enchantedAmethystShard.addLore(ItemRarity.UNCOMMON.getName());
        enchantedAmethystShard.addEnchantmentGlint();

        BlightedItem enchantedEmerald = new BlightedItem("ENCHANTED_EMERALD", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.EMERALD);
        enchantedEmerald.setDisplayName("Enchanted Emerald");
        enchantedEmerald.addLore(ItemRarity.UNCOMMON.getName());
        enchantedEmerald.addEnchantmentGlint();

        BlightedItem enchantedRottenFlesh = new BlightedItem("ENCHANTED_ROTTEN_FLESH", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.ROTTEN_FLESH);
        enchantedRottenFlesh.setDisplayName("Enchanted Rotten Flesh");
        enchantedRottenFlesh.addLore(ItemRarity.UNCOMMON.getName());
        enchantedRottenFlesh.addEnchantmentGlint();

        BlightedItem enchantedBone = new BlightedItem("ENCHANTED_BONE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.BONE);
        enchantedBone.setDisplayName("Enchanted Bone");
        enchantedBone.addLore(ItemRarity.UNCOMMON.getName());
        enchantedBone.addEnchantmentGlint();

        BlightedItem enchantedString = new BlightedItem("ENCHANTED_STRING", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.STRING);
        enchantedString.setDisplayName("Enchanted String");
        enchantedString.addLore(ItemRarity.UNCOMMON.getName());
        enchantedString.addEnchantmentGlint();

        BlightedItem enchantedGunpowder = new BlightedItem("ENCHANTED_GUNPOWDER", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.GUNPOWDER);
        enchantedGunpowder.setDisplayName("Enchanted Gunpowder");
        enchantedGunpowder.addLore(ItemRarity.UNCOMMON.getName());
        enchantedGunpowder.addEnchantmentGlint();

        BlightedItem enchantedSpiderEye = new BlightedItem("ENCHANTED_SPIDER_EYE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.SPIDER_EYE);
        enchantedSpiderEye.setDisplayName("Enchanted Spider Eye");
        enchantedSpiderEye.addLore(ItemRarity.UNCOMMON.getName());
        enchantedSpiderEye.addEnchantmentGlint();

        BlightedItem enchantedSlimeBall = new BlightedItem("ENCHANTED_SLIME_BALL", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.SLIME_BALL);
        enchantedSlimeBall.setDisplayName("Enchanted Slime Ball");
        enchantedSlimeBall.addLore(ItemRarity.UNCOMMON.getName());
        enchantedSlimeBall.addEnchantmentGlint();

        BlightedItem enchantedPhantomMembrane = new BlightedItem("ENCHANTED_PHANTOM_MEMBRANE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.PHANTOM_MEMBRANE);
        enchantedPhantomMembrane.setDisplayName("Enchanted Phantom Membrane");
        enchantedPhantomMembrane.addLore(ItemRarity.UNCOMMON.getName());
        enchantedPhantomMembrane.addEnchantmentGlint();

        BlightedItem enchantedResinClump = new BlightedItem("ENCHANTED_RESIN_CLUMP", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.RESIN_CLUMP);
        enchantedResinClump.setDisplayName("Enchanted Resin Clump");
        enchantedResinClump.addLore(ItemRarity.UNCOMMON.getName());
        enchantedResinClump.addEnchantmentGlint();

        BlightedItem enchantedCobblestone = new BlightedItem("ENCHANTED_COBBLESTONE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.COBBLESTONE);
        enchantedCobblestone.setDisplayName("Enchanted Cobblestone");
        enchantedCobblestone.addLore(ItemRarity.UNCOMMON.getName());
        enchantedCobblestone.addRule(ItemRule.PREVENT_PLACEMENT);
        enchantedCobblestone.addEnchantmentGlint();

        BlightedItem enchantedObsidian = new BlightedItem("ENCHANTED_OBSIDIAN", ItemType.MATERIAL, ItemRarity.RARE, Material.OBSIDIAN);
        enchantedObsidian.setDisplayName("Enchanted Obsidian");
        enchantedObsidian.addLore(ItemRarity.RARE.getName());
        enchantedObsidian.addRule(ItemRule.PREVENT_PLACEMENT);
        enchantedObsidian.addEnchantmentGlint();

        BlightedItem enchantedPaper = new BlightedItem("ENCHANTED_PAPER", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.PAPER);
        enchantedPaper.setDisplayName("Enchanted Paper");
        enchantedPaper.addLore(ItemRarity.UNCOMMON.getName());
        enchantedPaper.addEnchantmentGlint();

        BlightedItem enchantedClayBall = new BlightedItem("ENCHANTED_CLAY_BALL", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.CLAY_BALL);
        enchantedClayBall.setDisplayName("Enchanted Clay Ball");
        enchantedClayBall.addLore(ItemRarity.UNCOMMON.getName());
        enchantedClayBall.addEnchantmentGlint();

        BlightedItem enchantedCod = new BlightedItem("ENCHANTED_COD", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.COD);
        enchantedCod.setDisplayName("Enchanted Cod");
        enchantedCod.addLore(ItemRarity.UNCOMMON.getName());
        enchantedCod.addRule(ItemRule.PREVENT_CONSUME);
        enchantedCod.addEnchantmentGlint();

        BlightedItem enchantedSalmon = new BlightedItem("ENCHANTED_SALMON", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.SALMON);
        enchantedSalmon.setDisplayName("Enchanted Salmon");
        enchantedSalmon.addLore(ItemRarity.UNCOMMON.getName());
        enchantedSalmon.addRule(ItemRule.PREVENT_CONSUME);
        enchantedSalmon.addEnchantmentGlint();

        BlightedItem enchantedTropicalFish = new BlightedItem("ENCHANTED_TROPICAL_FISH", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.TROPICAL_FISH);
        enchantedTropicalFish.setDisplayName("Enchanted Tropical Fish");
        enchantedTropicalFish.addLore(ItemRarity.UNCOMMON.getName());
        enchantedTropicalFish.addRule(ItemRule.PREVENT_CONSUME);
        enchantedTropicalFish.addEnchantmentGlint();

        BlightedItem enchantedPufferfish = new BlightedItem("ENCHANTED_PUFFERFISH", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.PUFFERFISH);
        enchantedPufferfish.setDisplayName("Enchanted Pufferfish");
        enchantedPufferfish.addLore(ItemRarity.UNCOMMON.getName());
        enchantedPufferfish.addRule(ItemRule.PREVENT_CONSUME);
        enchantedPufferfish.addEnchantmentGlint();

        BlightedItem enchantedSeaPickle = new BlightedItem("ENCHANTED_SEA_PICKLE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.SEA_PICKLE);
        enchantedSeaPickle.setDisplayName("Enchanted Sea Pickle");
        enchantedSeaPickle.addLore(ItemRarity.UNCOMMON.getName());
        enchantedSeaPickle.addRule(ItemRule.PREVENT_PLACEMENT);
        enchantedSeaPickle.addEnchantmentGlint();

        add(
            enchantedIronIngot,
            enchantedIronBlock,
            enchantedCopperIngot,
            enchantedGoldIngot,
            enchantedLapisLazuli,
            enchantedRedstone,
            enchantedAmethystShard,
            enchantedEmerald,
            enchantedRottenFlesh,
            enchantedBone,
            enchantedString,
            enchantedGunpowder,
            enchantedSpiderEye,
            enchantedSlimeBall,
            enchantedPhantomMembrane,
            enchantedResinClump,
            enchantedCobblestone,
            enchantedObsidian,
            enchantedPaper,
            enchantedClayBall,
            enchantedCod,
            enchantedSalmon,
            enchantedTropicalFish,
            enchantedPufferfish,
            enchantedSeaPickle
        );
    }
}
