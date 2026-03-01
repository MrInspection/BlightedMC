package fr.moussax.blightedMC.smp.features.items.armors;

import fr.moussax.blightedMC.smp.core.items.BlightedItem;
import fr.moussax.blightedMC.smp.core.items.ItemRarity;
import fr.moussax.blightedMC.smp.core.items.ItemType;
import fr.moussax.blightedMC.smp.core.items.abilities.FullSetBonus;
import fr.moussax.blightedMC.smp.core.items.registry.ItemProvider;
import fr.moussax.blightedMC.smp.core.items.rules.ItemRule;
import fr.moussax.blightedMC.smp.features.items.abilities.weave.EmberWeaveSetBonus;
import fr.moussax.blightedMC.smp.features.items.abilities.weave.MagmaweaveSetBonus;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public final class FishingArmors implements ItemProvider {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void register() {

        FullSetBonus emberWeaveSetBonus = new EmberWeaveSetBonus();
        String[] emberWeaveSetBonusLore = new String[]{
            "",
            " §5Full Set Bonus: Molten Attunement ",
            " §7Impervious to the inferno, your",
            " §7heat signature synchronizes ",
            " §7with §6molten currents§7.",
            "",
            " §7Grants immunity to §cfire §7and §clava§7.",
            " §7Grants §b+15% §3Lava Fishing Speed§7.",
            ""
        };

        BlightedItem emberWeaveHelmet = new BlightedItem("EMBERWEAVE_HELMET", ItemType.HELMET,
            ItemRarity.RARE, Material.PLAYER_HEAD);

        emberWeaveHelmet.setDisplayName("Emberweave Helmet");
        emberWeaveHelmet.addLore(emberWeaveSetBonusLore);
        emberWeaveHelmet.addLore(ItemRarity.RARE.getName() + " HELMET");
        emberWeaveHelmet.addAttributeModifier(
            Attribute.ARMOR,
            2.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.HEAD
        );
        emberWeaveHelmet.addRule(ItemRule.PREVENT_PLACEMENT);
        emberWeaveHelmet.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY2M2RkY2JhZjdjNDQ4YTc2ODk4MWFiMDhkYTBmYWMyMzQ4MTU1N2M0ZTVhNTg2YWJmZTc1OWRmMTI3MWNhIn19fQ==");
        emberWeaveHelmet.editEquippable(equippable -> equippable.setSlot(EquipmentSlot.HEAD));
        emberWeaveHelmet.setFireResistant(true);

        BlightedItem emberWeaveChestplate = new BlightedItem("EMBERWEAVE_CHESTPLATE", ItemType.CHESTPLATE,
            ItemRarity.RARE, Material.LEATHER_CHESTPLATE);

        emberWeaveChestplate.setDisplayName("Emberweave Chestplate");
        emberWeaveChestplate.addLore(emberWeaveSetBonusLore);
        emberWeaveChestplate.addLore(ItemRarity.RARE.getName() + " CHESTPLATE");
        emberWeaveChestplate.addAttributeModifier(
            Attribute.ARMOR,
            6.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.CHEST
        );
        emberWeaveChestplate.addItemFlag(ItemFlag.HIDE_DYE);
        emberWeaveChestplate.setLeatherColor("#7A2724");
        emberWeaveChestplate.setFireResistant(true);
        emberWeaveChestplate.setMaxDurability(240);

        BlightedItem emberWeaveLeggins = new BlightedItem("EMBERWEAVE_LEGGINS", ItemType.LEGGINGS,
            ItemRarity.RARE, Material.LEATHER_LEGGINGS);

        emberWeaveLeggins.setDisplayName("Emberweave Leggings");
        emberWeaveLeggins.addLore(emberWeaveSetBonusLore);
        emberWeaveLeggins.addLore(ItemRarity.RARE.getName() + " LEGGINGS");
        emberWeaveLeggins.addAttributeModifier(
            Attribute.ARMOR,
            5.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.LEGS
        );
        emberWeaveLeggins.addItemFlag(ItemFlag.HIDE_DYE);
        emberWeaveLeggins.setLeatherColor("#7A2724");
        emberWeaveLeggins.setFireResistant(true);
        emberWeaveLeggins.setMaxDurability(225);

        BlightedItem emberWeaveBoots = new BlightedItem("EMBERWEAVE_BOOTS", ItemType.BOOTS,
            ItemRarity.RARE, Material.LEATHER_BOOTS);

        emberWeaveBoots.setDisplayName("Emberweave Boots");
        emberWeaveBoots.addLore(emberWeaveSetBonusLore);
        emberWeaveBoots.addLore(ItemRarity.RARE.getName() + " BOOTS");
        emberWeaveBoots.addAttributeModifier(
            Attribute.ARMOR,
            2.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.FEET
        );
        emberWeaveBoots.addItemFlag(ItemFlag.HIDE_DYE);
        emberWeaveBoots.setLeatherColor("#7A2724");
        emberWeaveBoots.setFireResistant(true);
        emberWeaveBoots.setMaxDurability(195);

        emberWeaveHelmet.setFullSetBonus(emberWeaveSetBonus);
        emberWeaveChestplate.setFullSetBonus(emberWeaveSetBonus);
        emberWeaveLeggins.setFullSetBonus(emberWeaveSetBonus);
        emberWeaveBoots.setFullSetBonus(emberWeaveSetBonus);

        FullSetBonus magmaWeaveSetBonus = new MagmaweaveSetBonus();
        String[] magmaWeaveSetBonusLore = new String[]{
            "",
            " §5Full Set Bonus: Blazing Shield",
            " §7Flames coalesce into a protective ",
            " §7mantle, fortifying you against",
            " §7hostile attacks.",
            "",
            " §7Grants immunity to §cfire §7and §clava§7. ",
            " §7Grants §dResistance §5I §7while in lava. ",
            " §7Grants §b+30% §3Lava Fishing Speed§7.",
            "",
        };

        BlightedItem ashfangHelmet = new BlightedItem("ASHFANG_HELMET", ItemType.HELMET,
            ItemRarity.EPIC, Material.PLAYER_HEAD);

        ashfangHelmet.setDisplayName("Ashfang Helmet");
        ashfangHelmet.addLore(magmaWeaveSetBonusLore);
        ashfangHelmet.addLore(ItemRarity.EPIC.getName() + " HELMET");
        ashfangHelmet.addAttributeModifier(
            Attribute.ARMOR,
            3.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.HEAD
        );
        ashfangHelmet.addAttributeModifier(
            Attribute.ARMOR_TOUGHNESS,
            2.0, AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.HEAD
        );
        ashfangHelmet.editEquippable(equippable -> equippable.setSlot(EquipmentSlot.HEAD));
        ashfangHelmet.addRule(ItemRule.PREVENT_PLACEMENT);
        ashfangHelmet.setCustomSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNlNTkyNTBjOTY4ZTUwODUzOWJmMmU4NDkyZjU2YTJmNjY0ZWZiMzA5ZjQ5NWEwN2RjM2E1NGM4YjZhMjQ5ZSJ9fX0=");
        ashfangHelmet.setFireResistant(true);


        BlightedItem ashfangChestplate = new BlightedItem("ASHFANG_CHESTPLATE", ItemType.CHESTPLATE, ItemRarity.EPIC, Material.LEATHER_CHESTPLATE);
        ashfangChestplate.setDisplayName("Ashfang Chestplate");
        ashfangChestplate.setLeatherColor("#420905");
        ashfangChestplate.addLore(magmaWeaveSetBonusLore);
        ashfangChestplate.addLore(ItemRarity.EPIC.getName() + " CHESTPLATE");
        ashfangChestplate.addAttributeModifier(
            Attribute.ARMOR,
            8.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.CHEST
        );
        ashfangChestplate.addAttributeModifier(
            Attribute.ARMOR_TOUGHNESS,
            2.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.CHEST
        );
        ashfangChestplate.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
        ashfangChestplate.setArmorTrim(TrimMaterial.RESIN, TrimPattern.FLOW);
        ashfangChestplate.setFireResistant(true);
        ashfangChestplate.setMaxDurability(528);


        BlightedItem ashfangLeggins = new BlightedItem("ASHFANG_LEGGINS", ItemType.LEGGINGS,
            ItemRarity.EPIC, Material.LEATHER_LEGGINGS);

        ashfangLeggins.setDisplayName("Ashfang Leggings");
        ashfangLeggins.addLore(magmaWeaveSetBonusLore);
        ashfangLeggins.addLore(ItemRarity.EPIC.getName() + " LEGGINGS");
        ashfangLeggins.addAttributeModifier(
            Attribute.ARMOR,
            6.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.LEGS
        );
        ashfangLeggins.addAttributeModifier(
            Attribute.ARMOR_TOUGHNESS,
            2.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.LEGS
        );
        ashfangLeggins.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
        ashfangLeggins.setArmorTrim(TrimMaterial.RESIN, TrimPattern.FLOW);
        ashfangLeggins.setLeatherColor("#420905");
        ashfangLeggins.setFireResistant(true);
        ashfangLeggins.setMaxDurability(495);

        BlightedItem ashfangBoots = new BlightedItem("ASHFANG_BOOTS", ItemType.BOOTS,
            ItemRarity.EPIC, Material.LEATHER_BOOTS);

        ashfangBoots.setDisplayName("Ashfang Boots");
        ashfangBoots.addLore(magmaWeaveSetBonusLore);
        ashfangBoots.addLore(ItemRarity.EPIC.getName() + " BOOTS");
        ashfangBoots.addAttributeModifier(
            Attribute.ARMOR,
            3.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.FEET
        );
        ashfangBoots.addAttributeModifier(
            Attribute.ARMOR_TOUGHNESS,
            2.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.FEET
        );
        ashfangBoots.addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
        ashfangBoots.setArmorTrim(TrimMaterial.RESIN, TrimPattern.FLOW);
        ashfangBoots.setLeatherColor("#420905");
        ashfangBoots.setFireResistant(true);
        ashfangBoots.setMaxDurability(429);

        ashfangHelmet.setFullSetBonus(magmaWeaveSetBonus);
        ashfangChestplate.setFullSetBonus(magmaWeaveSetBonus);
        ashfangLeggins.setFullSetBonus(magmaWeaveSetBonus);
        ashfangBoots.setFullSetBonus(magmaWeaveSetBonus);

        String[] anglerLore = new String[]{
            "",
            " §7Worn by those who cast lines ",
            " §7into the §9Deep Seas§7. A relic of ",
            " §7forgotten tradition, guiding",
            " §7the path toward mastery.",
            "",
        };

        BlightedItem anglerHelmet = new BlightedItem("ANGLER_HELMET", ItemType.HELMET,
            ItemRarity.UNCOMMON, Material.COPPER_HELMET);

        anglerHelmet.setDisplayName("Angler Helmet");
        anglerHelmet.addLore(anglerLore);
        anglerHelmet.addLore(ItemRarity.UNCOMMON.getName() + " HELMET");
        anglerHelmet.addAttributeModifier(
            Attribute.ARMOR,
            2.0,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.HEAD
        );
        anglerHelmet.addAttributeModifier(
            Attribute.OXYGEN_BONUS,
            0.5,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.HEAD
        );
        anglerHelmet.addAttributeModifier(
            Attribute.LUCK,
            0.25,
            AttributeModifier.Operation.ADD_NUMBER,
            EquipmentSlotGroup.HEAD
        );
        anglerHelmet.setMaxDurability(121);

        BlightedItem anglerChestplate = new BlightedItem("ANGLER_CHESTPLATE", ItemType.CHESTPLATE,
            ItemRarity.UNCOMMON, Material.LEATHER_CHESTPLATE);

        anglerChestplate.setDisplayName("Angler Chestplate");
        anglerChestplate.addLore(anglerLore);
        anglerChestplate.addLore(ItemRarity.UNCOMMON.getName() + " CHESTPLATE");
        anglerChestplate.addAttributeModifier(Attribute.ARMOR, 4.0,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
        anglerChestplate.addAttributeModifier(Attribute.OXYGEN_BONUS, 0.5,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
        anglerChestplate.addAttributeModifier(Attribute.LUCK, 0.25,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.CHEST);
        anglerChestplate.addItemFlag(ItemFlag.HIDE_DYE);
        anglerChestplate.setLeatherColor("#4B6D9E");
        anglerChestplate.setMaxDurability(176);

        BlightedItem anglerLeggings = new BlightedItem("ANGLER_LEGGINGS", ItemType.LEGGINGS,
            ItemRarity.UNCOMMON, Material.LEATHER_LEGGINGS);

        anglerLeggings.setDisplayName("Angler Leggings");
        anglerLeggings.addLore(anglerLore);
        anglerLeggings.addLore(ItemRarity.UNCOMMON.getName() + " LEGGINGS");
        anglerLeggings.addAttributeModifier(Attribute.ARMOR, 3.0,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
        anglerLeggings.addAttributeModifier(Attribute.OXYGEN_BONUS, 0.5,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
        anglerLeggings.addAttributeModifier(Attribute.LUCK, 0.25,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.LEGS);
        anglerLeggings.addItemFlag(ItemFlag.HIDE_DYE);
        anglerLeggings.setLeatherColor("#4B6D9E");
        anglerLeggings.setMaxDurability(165);

        BlightedItem anglerBoots = new BlightedItem("ANGLER_BOOTS", ItemType.BOOTS,
            ItemRarity.UNCOMMON, Material.LEATHER_BOOTS);

        anglerBoots.setDisplayName("Angler Boots");
        anglerBoots.addLore(anglerLore);
        anglerBoots.addLore(ItemRarity.UNCOMMON.getName() + " BOOTS");
        anglerBoots.addAttributeModifier(Attribute.ARMOR, 1.0,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
        anglerBoots.addAttributeModifier(Attribute.OXYGEN_BONUS, 0.5,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
        anglerBoots.addAttributeModifier(Attribute.LUCK, 0.25,
            AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.FEET);
        anglerBoots.addItemFlag(ItemFlag.HIDE_DYE);
        anglerBoots.setLeatherColor("#4B6D9E");
        anglerBoots.setMaxDurability(143);

        add(
            emberWeaveHelmet,
            emberWeaveChestplate,
            emberWeaveLeggins,
            emberWeaveBoots,
            ashfangHelmet,
            ashfangChestplate,
            ashfangLeggins,
            ashfangBoots,
            anglerHelmet,
            anglerChestplate,
            anglerLeggings,
            anglerBoots
        );
    }
}
