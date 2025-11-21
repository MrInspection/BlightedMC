package fr.moussax.blightedMC.gameplay.entities.spawnable;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.core.entities.loot.LootDropRarity;
import fr.moussax.blightedMC.core.entities.loot.LootTable;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnConditions;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class BlightedZombie extends SpawnableEntity {
    private BukkitRunnable speedAbilityTask;
    private boolean hasSpeedBoost = false;

    public BlightedZombie() {
        super("BLIGHTED_ZOMBIE", "Blighted Zombie", 30, EntityType.ZOMBIE, 0.05);
        setNameTagType(EntityNameTag.BLIGHTED);
        setDamage(15);
        setDroppedExp(10);
        setLootTable(createLootTable());

        armor = new ItemStack[]{
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor("#81CFE2").toItemStack(),
                new ItemBuilder(Material.LEATHER_HELMET).setLeatherColor("#81CFE2").toItemStack()
        };

        addRepeatingTask(() -> {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity == null || entity.isDead()) {
                        stopSpeedAbility();
                        cancel();
                        return;
                    }
                    startSpeedAbility();
                }
            };
            speedAbilityTask = task;
            return task;
        }, 100L, 200L);
    }

    @Override
    protected void setupSpawnConditions() {
        addSpawnCondition(
                SpawnConditions.biome(Biome.FOREST, Biome.BIRCH_FOREST, Biome.DARK_FOREST, Biome.FLOWER_FOREST, Biome.SAVANNA)
                        .and(SpawnConditions.nightTime())
                        .and(SpawnConditions.clearWeather())
                        .and(SpawnConditions.minY(50))
                        .and(SpawnConditions.maxY(80))
                        .and(SpawnConditions.skyExposed())
                        .and(SpawnConditions.notInWater())
        );
    }

    @Override
    public SpawnableEntity clone() {
        BlightedZombie clone = (BlightedZombie) super.clone();
        clone.speedAbilityTask = null;
        clone.hasSpeedBoost = false;

        clone.addRepeatingTask(() -> {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (clone.entity == null || clone.entity.isDead()) {
                        clone.stopSpeedAbility();
                        cancel();
                        return;
                    }
                    clone.startSpeedAbility();
                }
            };
            clone.speedAbilityTask = task;
            return task;
        }, 100L, 200L);

        return clone;
    }

    @Override
    public LivingEntity spawn(Location location) {
        super.spawn(location);
        return entity;
    }

    private void startSpeedAbility() {
        if (hasSpeedBoost || entity == null) return;

        hasSpeedBoost = true;
        equipSpeedArmor();
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2));

        new BukkitRunnable() {
            @Override
            public void run() {
                hasSpeedBoost = false;
                equipNormalArmor();
            }
        }.runTaskLater(BlightedMC.getInstance(), 200);
    }

    private void stopSpeedAbility() {
        if (speedAbilityTask != null) {
            try {
                speedAbilityTask.cancel();
            } catch (IllegalStateException ignored) {
            }
            speedAbilityTask = null;
        }
        hasSpeedBoost = false;
        if (entity != null && !entity.isDead()) {
            equipNormalArmor();
        }
    }

    @Override
    public void kill() {
        stopSpeedAbility();
        super.kill();
    }

    private void equipSpeedArmor() {
        if (!(entity instanceof Zombie zombie)) return;

        Objects.requireNonNull(zombie.getEquipment()).setChestplate(
                new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .setLeatherColor("#81CFE2")
                        .setArmorTrim(TrimMaterial.DIAMOND, TrimPattern.FLOW)
                        .toItemStack()
        );
        zombie.getEquipment().setHelmet(
                new ItemBuilder(Material.LEATHER_HELMET)
                        .setLeatherColor("#81CFE2")
                        .setArmorTrim(TrimMaterial.DIAMOND, TrimPattern.FLOW)
                        .toItemStack()
        );
    }

    private void equipNormalArmor() {
        if (!(entity instanceof Zombie zombie)) return;

        Objects.requireNonNull(zombie.getEquipment()).setChestplate(
                new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor("#81CFE2").toItemStack()
        );
        zombie.getEquipment().setHelmet(
                new ItemBuilder(Material.LEATHER_HELMET).setLeatherColor("#81CFE2").toItemStack()
        );
    }

    private LootTable createLootTable() {
        return new LootTable()
                .setMaxDrop(2)
                .addLoot(Material.ROTTEN_FLESH, 1, 3, 1, LootDropRarity.COMMON)
                .addGemsLoot(5, 0.03, LootDropRarity.EXTRAORDINARY);
    }
}
