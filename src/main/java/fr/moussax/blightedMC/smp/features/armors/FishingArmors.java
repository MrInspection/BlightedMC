package fr.moussax.blightedMC.smp.features.armors;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import fr.moussax.blightedMC.smp.features.abilities.weave.EmberWeaveSetBonus;
import fr.moussax.blightedMC.smp.features.abilities.weave.MagmaweaveSetBonus;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public final class FishingArmors implements ItemProvider {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void register() {
        String[] emberWeaveSetBonusLore = new String[]{
            " §5Full Set Bonus: Molten Attunement ",
            " §7Impervious to the inferno, your",
            " §7heat signature synchronizes ",
            " §7with §6molten currents§7.",
            "",
            " §7Grants immunity to §cfire §7and §clava§7.",
            " §7Grants §b+15% §3Lava Fishing Speed§7.",
            "",
            " §8“To walk the inferno, one must",
            " §8become the flame.”",
        };

        String[] magmaWeaveSetBonusLore = new String[]{
            " §5Full Set Bonus: Molten Shell ",
            " §7The magma hardens your skin,",
            " §7deflecting incoming attacks.",
            "",
            " §7Grants immunity to §cfire §7and §clava§7. ",
            " §7Grants §dResistance §5I §7while in lava. ",
            " §7Grants §b+30% §3Lava Fishing Speed§7.",
            "",
            " §8“Harder than obsidian.”",
        };

        FullSetBonus emberWeaveSetBonus = new EmberWeaveSetBonus();
        FullSetBonus magmaWeaveSetBonus = new MagmaweaveSetBonus();

        BlightedItem emberWeaveHelmet = new BlightedItem("EMBERWEAVE_HELMET", ItemType.HELMET, ItemRarity.RARE, Material.PLAYER_HEAD);
        emberWeaveHelmet.setDisplayName("Emberweave Helmet");
        emberWeaveHelmet.addLore("");
        emberWeaveHelmet.addLore(emberWeaveSetBonusLore);
        emberWeaveHelmet.addLore("", ItemRarity.RARE.getName() + " HELMET");
        emberWeaveHelmet.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2QxNWUwY2Y3YjkyMmM2N2RkZjU3M2UzNDg1NDYzYWY3Zjg0ODBhYmMwMWIzOGU3ZTFiYTZiYjRmMGQ2ODRmZCJ9fX0=");
        emberWeaveHelmet.addRule(ItemRule.PREVENT_PLACEMENT);
        emberWeaveHelmet.setFireResistant(true);
        emberWeaveHelmet.addAttributeModifier(
            Attribute.ARMOR,
            2.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.HEAD
        );

        BlightedItem emberWeaveChestplate = new BlightedItem("EMBERWEAVE_CHESTPLATE", ItemType.CHESTPLATE, ItemRarity.RARE, Material.LEATHER_CHESTPLATE);
        emberWeaveChestplate.setDisplayName("Emberweave Chestplate");
        emberWeaveChestplate.setLeatherColor("#7A2724");
        emberWeaveChestplate.addLore("");
        emberWeaveChestplate.addLore(emberWeaveSetBonusLore);
        emberWeaveChestplate.addLore("", ItemRarity.RARE.getName() + " CHESTPLATE");
        emberWeaveChestplate.addItemFlag(ItemFlag.HIDE_DYE);
        emberWeaveChestplate.setFireResistant(true);
        emberWeaveChestplate.setMaxDurability(240);
        emberWeaveChestplate.addAttributeModifier(
            Attribute.ARMOR,
            6.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.CHEST
        );

        BlightedItem emberWeaveLeggins = new BlightedItem("EMBERWEAVE_LEGGINS", ItemType.LEGGINGS, ItemRarity.RARE, Material.LEATHER_LEGGINGS);
        emberWeaveLeggins.setDisplayName("Emberweave Leggings");
        emberWeaveLeggins.setLeatherColor("#7A2724");
        emberWeaveLeggins.addLore("");
        emberWeaveLeggins.addLore(emberWeaveSetBonusLore);
        emberWeaveLeggins.addLore("", ItemRarity.RARE.getName() + " LEGGINGS");
        emberWeaveLeggins.addItemFlag(ItemFlag.HIDE_DYE);
        emberWeaveLeggins.setFireResistant(true);
        emberWeaveLeggins.setMaxDurability(225);
        emberWeaveLeggins.addAttributeModifier(
            Attribute.ARMOR,
            5.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.LEGS
        );

        BlightedItem emberWeaveBoots = new BlightedItem("EMBERWEAVE_BOOTS", ItemType.BOOTS, ItemRarity.RARE, Material.LEATHER_BOOTS);
        emberWeaveBoots.setDisplayName("Emberweave Boots");
        emberWeaveBoots.addLore("");
        emberWeaveBoots.addLore(emberWeaveSetBonusLore);
        emberWeaveBoots.addLore("", ItemRarity.RARE.getName() + " BOOTS");
        emberWeaveBoots.addItemFlag(ItemFlag.HIDE_DYE);
        emberWeaveBoots.setLeatherColor("#7A2724");
        emberWeaveBoots.setFireResistant(true);
        emberWeaveBoots.setMaxDurability(195);
        emberWeaveBoots.addAttributeModifier(
            Attribute.ARMOR,
            2.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.FEET
        );

        emberWeaveHelmet.setFullSetBonus(emberWeaveSetBonus);
        emberWeaveChestplate.setFullSetBonus(emberWeaveSetBonus);
        emberWeaveLeggins.setFullSetBonus(emberWeaveSetBonus);
        emberWeaveBoots.setFullSetBonus(emberWeaveSetBonus);

        BlightedItem magmaWeaveHelmet = new BlightedItem("MAGMAWEAVE_HELMET", ItemType.HELMET, ItemRarity.EPIC, Material.PLAYER_HEAD);
        magmaWeaveHelmet.setDisplayName("⚚ Magmaweave Helmet");
        magmaWeaveHelmet.addLore("");
        magmaWeaveHelmet.addLore(magmaWeaveSetBonusLore);
        magmaWeaveHelmet.addLore("", ItemRarity.EPIC.getName() + " HELMET");
        magmaWeaveHelmet.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNlNTkyNTBjOTY4ZTUwODUzOWJmMmU4NDkyZjU2YTJmNjY0ZWZiMzA5ZjQ5NWEwN2RjM2E1NGM4YjZhMjQ5ZSJ9fX0=");
        magmaWeaveHelmet.addRule(ItemRule.PREVENT_PLACEMENT);
        magmaWeaveHelmet.setFireResistant(true);
        magmaWeaveHelmet.addAttributeModifier(Attribute.ARMOR, 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
        magmaWeaveHelmet.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HEAD);
        magmaWeaveHelmet.addEnchantment(Enchantment.PROTECTION, 4);
        magmaWeaveHelmet.addEnchantment(Enchantment.PROJECTILE_PROTECTION, 4);

        BlightedItem magmaWeaveChestplate = new BlightedItem("MAGMAWEAVE_CHESTPLATE", ItemType.CHESTPLATE, ItemRarity.EPIC, Material.LEATHER_CHESTPLATE);
        magmaWeaveChestplate.setDisplayName("⚚ Magmaweave Chestplate");
        magmaWeaveChestplate.setLeatherColor("#420905");
        magmaWeaveChestplate.addLore("");
        magmaWeaveChestplate.addLore(magmaWeaveSetBonusLore);
        magmaWeaveChestplate.addLore("", ItemRarity.EPIC.getName() + " CHESTPLATE");
        magmaWeaveChestplate.addAttributeModifier(Attribute.ARMOR, 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
        magmaWeaveChestplate.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
        magmaWeaveChestplate.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
        magmaWeaveChestplate.setFireResistant(true);
        magmaWeaveChestplate.setMaxDurability(528);
        magmaWeaveChestplate.setArmorTrim(TrimMaterial.RESIN, TrimPattern.FLOW);

        BlightedItem magmaWeaveLeggins = new BlightedItem("MAGMAWEAVE_LEGGINS", ItemType.LEGGINGS, ItemRarity.EPIC, Material.LEATHER_LEGGINGS);
        magmaWeaveLeggins.setDisplayName("⚚ Magmaweave Leggings");
        magmaWeaveLeggins.addLore("");
        magmaWeaveLeggins.addLore(magmaWeaveSetBonusLore);
        magmaWeaveLeggins.addLore("", ItemRarity.EPIC.getName() + " LEGGINGS");
        magmaWeaveLeggins.addAttributeModifier(Attribute.ARMOR, 6.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
        magmaWeaveLeggins.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
        magmaWeaveLeggins.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
        magmaWeaveLeggins.setLeatherColor("#420905");
        magmaWeaveLeggins.setFireResistant(true);
        magmaWeaveLeggins.setMaxDurability(495);
        magmaWeaveLeggins.setArmorTrim(TrimMaterial.RESIN, TrimPattern.FLOW);

        BlightedItem magmaweaveBoots = new BlightedItem("MAGMAWEAVE_BOOTS", ItemType.BOOTS, ItemRarity.EPIC, Material.LEATHER_BOOTS);
        magmaweaveBoots.setDisplayName("⚚ Magmaweave Boots");
        magmaweaveBoots.addLore("");
        magmaweaveBoots.addLore(magmaWeaveSetBonusLore);
        magmaweaveBoots.addLore("", ItemRarity.EPIC.getName() + " BOOTS");
        magmaweaveBoots.addAttributeModifier(Attribute.ARMOR, 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
        magmaweaveBoots.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
        magmaweaveBoots.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
        magmaweaveBoots.setLeatherColor("#420905");
        magmaweaveBoots.setFireResistant(true);
        magmaweaveBoots.setMaxDurability(429);
        magmaweaveBoots.setArmorTrim(TrimMaterial.RESIN, TrimPattern.FLOW);

        magmaWeaveHelmet.setFullSetBonus(magmaWeaveSetBonus);
        magmaWeaveChestplate.setFullSetBonus(magmaWeaveSetBonus);
        magmaWeaveLeggins.setFullSetBonus(magmaWeaveSetBonus);
        magmaweaveBoots.setFullSetBonus(magmaWeaveSetBonus);

        BlightedItem anglerHelmet = new BlightedItem("ANGLER_HELMET", ItemType.HELMET, ItemRarity.UNCOMMON, Material.DIAMOND_HELMET);
        BlightedItem anglerChestplate = new BlightedItem("ANGLER_CHESTPLATE", ItemType.CHESTPLATE, ItemRarity.UNCOMMON, Material.LEATHER_CHESTPLATE);
        BlightedItem anglerLeggings = new BlightedItem("ANGLER_LEGGINGS", ItemType.LEGGINGS, ItemRarity.UNCOMMON, Material.LEATHER_LEGGINGS);
        BlightedItem anglerBoots = new BlightedItem("ANGLER_BOOTS", ItemType.BOOTS, ItemRarity.UNCOMMON, Material.LEATHER_BOOTS);

        anglerChestplate.setLeatherColor("#4B6D9E");
        anglerLeggings.setLeatherColor("#4B6D9E");
        anglerBoots.setLeatherColor("#4B6D9E");

        add(
            emberWeaveHelmet,
            emberWeaveChestplate,
            emberWeaveLeggins,
            emberWeaveBoots,
            magmaWeaveHelmet,
            magmaWeaveChestplate,
            magmaWeaveLeggins,
            magmaweaveBoots
        );
    }
}
