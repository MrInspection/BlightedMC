package fr.moussax.blightedSMP.content.items.materials;

import fr.moussax.blightedSMP.engine.items.BlightedItem;
import fr.moussax.blightedSMP.engine.items.ItemRarity;
import fr.moussax.blightedSMP.engine.items.ItemType;
import fr.moussax.blightedSMP.engine.items.rules.ItemRule;
import fr.moussax.blightedSMP.registry.RegistryModule;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class FishingMaterials implements RegistryModule<Consumer<BlightedItem>> {

    @Override
    public void register(Consumer<BlightedItem> registry) {

        BlightedItem blightedAlgae = new BlightedItem("BLIGHTED_ALGAE", ItemType.MATERIAL, ItemRarity.UNCOMMON, Material.KELP);
        blightedAlgae.setDisplayName("Blighted Algae");
        blightedAlgae.addLore(
                "",
                " §7A thick, dark strand of kelp ",
                " §7that grows in waters heavy with ",
                " §7corruption. Pulsates with a dim, ",
                " §7dark glow.",
                "",
                ItemRarity.UNCOMMON.getName()
        );
        blightedAlgae.addRule(ItemRule.PREVENT_PLACEMENT);
        blightedAlgae.addEnchantmentGlint();

        BlightedItem smokedSalmonPlate = new BlightedItem("SMOKED_SALMON_PLATE", ItemType.UNCATEGORIZED, ItemRarity.UNCOMMON, Material.COOKED_SALMON);
        smokedSalmonPlate.setDisplayName("Smoked Salmon Plate");
        smokedSalmonPlate.addLore(
                "",
                " §7A seasoned, perfectly smoked",
                " §7salmon fillet. Tastes of wild sea ",
                " §7herbs and hickory wood.",
                "",
                ItemRarity.UNCOMMON.getName()
        );
        smokedSalmonPlate.editFood(food -> {
            food.setNutrition(8);
            food.setSaturation(12.8f);
            food.setCanAlwaysEat(true);
        });

        BlightedItem saltedCod = new BlightedItem("SALTED_COD", ItemType.UNCATEGORIZED, ItemRarity.UNCOMMON, Material.COOKED_COD);
        saltedCod.setDisplayName("Salted Cod");
        saltedCod.addLore(
                "",
                " §7Sun-dried cod heavily encrusted in ",
                " §7coarse sea salt. Keeps indefinitely",
                " §7and fills the belly.",
                "",
                ItemRarity.UNCOMMON.getName()
        );
        saltedCod.editFood(food -> {
            food.setNutrition(7);
            food.setSaturation(11.2f);
            food.setCanAlwaysEat(true);
        });

        BlightedItem fishermansBait = new BlightedItem("FISHERMANS_BAIT", ItemType.UNCATEGORIZED, ItemRarity.UNCOMMON, Material.HONEY_BOTTLE);
        fishermansBait.setDisplayName("Fisherman's Bait");
        fishermansBait.addLore(
                "",
                " §7A sweet, pungent mixture of",
                " §7golden honey and ground bugs. ",
                " §7Irresistible to aquatic life.",
                "",
                ItemRarity.UNCOMMON.getName()
        );
        fishermansBait.addRule(ItemRule.PREVENT_CONSUME);

        BlightedItem fishermansStew = new BlightedItem("FISHERMANS_STEW", ItemType.UNCATEGORIZED, ItemRarity.UNCOMMON, Material.RABBIT_STEW);
        fishermansStew.setDisplayName("Fisherman's Stew");
        fishermansStew.addLore(
                "",
                " §7A hearty stew prepared from",
                " §7fresh catches. Provides the ",
                " §7consumer with aquatic affinity. ",
                "",
                " §8When Consumed:",
                " §8 ‣ &#8EBAFFWater Breathing I §8(2:00)",
                "",
                ItemRarity.UNCOMMON.getName()
        );
        fishermansStew.editFood(food -> {
            food.setNutrition(10);
            food.setSaturation(14.4f);
            food.setCanAlwaysEat(true);
        });
        fishermansStew.onConsume(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 2400, 0)));

        BlightedItem barnacleCluster = new BlightedItem("BARNACLE_CLUSTER", ItemType.MATERIAL, ItemRarity.RARE, Material.NAUTILUS_SHELL);
        barnacleCluster.setDisplayName("Barnacle Cluster");
        barnacleCluster.addLore(
                "",
                " §7A hardened mass of razor-sharp ",
                " §7barnacles clinging to a glowing ",
                " §7fossilized shell.",
                "",
                ItemRarity.RARE.getName()
        );
        barnacleCluster.addEnchantmentGlint();

        BlightedItem coralFragment = new BlightedItem("CORAL_FRAGMENT", ItemType.MATERIAL, ItemRarity.RARE, Material.FIRE_CORAL);
        coralFragment.setDisplayName("Coral Fragment");
        coralFragment.addLore(
                "",
                " §7A bright, warm fragment of coral",
                " §7retrieved from the depths. Retains ",
                " §7a mystical heat.",
                "",
                ItemRarity.RARE.getName()
        );
        coralFragment.addRule(ItemRule.PREVENT_PLACEMENT);
        coralFragment.addEnchantmentGlint();

        BlightedItem messageInABottle = new BlightedItem("MESSAGE_IN_A_BOTTLE", ItemType.UNCATEGORIZED, ItemRarity.RARE, Material.GLASS_BOTTLE);
        messageInABottle.setDisplayName("Message in a Bottle");
        messageInABottle.addLore(
                "",
                " §7A sealed glass bottle containing",
                " §7a worn, waterlogged note. Perhaps ",
                " §7someone's final words.",
                "",
                " §fRight click to shatter!",
                "",
                ItemRarity.RARE.getName()
        );
        messageInABottle.addRule(ItemRule.PREVENT_PLACEMENT);

        BlightedItem blightedSushi = new BlightedItem("BLIGHTED_SUSHI", ItemType.UNCATEGORIZED, ItemRarity.RARE, Material.SUSPICIOUS_STEW);
        blightedSushi.setDisplayName("Blighted Sushi");
        blightedSushi.addLore(
                "",
                " §7A questionable sushi roll ",
                " §7wrapped in algae. It hums with ",
                " §7unstable entropic energy.",
                "",
                " §8When Consumed:",
                " §8 ‣ &#8EBAFFWater Breathing I §8(1:00)",
                " §8 ‣ &#FFCEB8Hunger I §8(0:15) §7(∼50%)",
                "",
                ItemRarity.RARE.getName()
        );
        blightedSushi.editFood(food -> {
            food.setNutrition(6);
            food.setSaturation(4.8f);
            food.setCanAlwaysEat(true);
        });
        blightedSushi.onConsume(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 1200, 0));
            if (ThreadLocalRandom.current().nextDouble() < 0.5) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 300, 0));
            }
        });
        blightedSushi.addEnchantmentGlint();

        BlightedItem abyssalPearl = new BlightedItem("ABYSSAL_PEARL", ItemType.MATERIAL, ItemRarity.EPIC, Material.ENDER_PEARL);
        abyssalPearl.setDisplayName("Abyssal Pearl");
        abyssalPearl.addLore(
                "",
                " &#F1B8FFA dark, swirling orb pulled from ",
                " &#F1B8FFthe abyss. Its core feels freezing ",
                " &#F1B8FFcold and infinitely deep.",
                "",
                ItemRarity.EPIC.getName()
        );
        abyssalPearl.addRule(ItemRule.PREVENT_PROJECTILE_LAUNCH);
        abyssalPearl.addEnchantmentGlint();

        BlightedItem drownedResearchCodex = new BlightedItem("DROWNED_RESEARCH_CODEX", ItemType.UNCATEGORIZED, ItemRarity.EPIC, Material.KNOWLEDGE_BOOK);
        drownedResearchCodex.setDisplayName("Drowned Research Codex");
        drownedResearchCodex.addLore(
                "",
                " §7A preserved collection of research ",
                " §7behind by an unknown explorer. Its ",
                " §7pages reveal forgotten secrets",
                " §7of the abyssal currents and the",
                " §7creatures lurking beneath.",
                "",
                ItemRarity.EPIC.getName()
        );
        drownedResearchCodex.setEnchantmentGlint(false);

        registry.accept(blightedAlgae);
        registry.accept(smokedSalmonPlate);
        registry.accept(saltedCod);
        registry.accept(fishermansBait);
        registry.accept(fishermansStew);
        registry.accept(barnacleCluster);
        registry.accept(coralFragment);
        registry.accept(messageInABottle);
        registry.accept(drownedResearchCodex);
        registry.accept(blightedSushi);
        registry.accept(abyssalPearl);
    }
}
