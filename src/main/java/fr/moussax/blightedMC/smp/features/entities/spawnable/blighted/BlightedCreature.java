package fr.moussax.blightedMC.smp.features.entities.spawnable.blighted;

import fr.moussax.blightedMC.smp.core.entities.EntityNameTag;
import fr.moussax.blightedMC.smp.core.entities.spawnable.SpawnableEntity;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public sealed abstract class BlightedCreature extends SpawnableEntity
    permits BlightedBogged, BlightedDrowned, BlightedHusk, BlightedParched, BlightedPiglin,
    BlightedSkeleton, BlightedStray, BlightedWitherSkeleton, BlightedZombie, BlightedZombifiedPiglin {

    private static final double ENRAGE_THRESHOLD = 0.50;

    protected boolean isEnraged;
    private int particleTicks = 0;

    protected BlightedCreature(String entityId, String name, EntityType entityType) {
        super(entityId, name, 30, entityType, 0.04);
        setNameTagType(EntityNameTag.BLIGHTED);
        setupDefaultArmor();
        setupEnrageTask();
    }

    private void setupDefaultArmor() {
        armor = new ItemStack[]{
            new ItemStack(Material.AIR),
            new ItemStack(Material.AIR),
            new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor("#B584D4").toItemStack(),
            new ItemBuilder(Material.LEATHER_HELMET).setLeatherColor("#A565C9").toItemStack()
        };
    }

    private void setupEnrageTask() {
        addRepeatingTask(() -> new BukkitRunnable() {
            @Override
            public void run() {
                if (entity == null || entity.isDead()) {
                    cancel();
                    return;
                }

                if (!isEnraged) {
                    checkHealthAndEnrage();
                } else if (++particleTicks >= 5) {
                    playEnrageParticles();
                    particleTicks = 0;
                }
            }
        }, 1L, 1L);
    }

    protected abstract void onEnrage(LivingEntity entity);

    private void checkHealthAndEnrage() {
        if (entity == null) return;

        double maxHealth = Objects.requireNonNull(entity
            .getAttribute(Attribute.MAX_HEALTH)).getValue();

        if (entity.getHealth() / maxHealth <= ENRAGE_THRESHOLD) {
            triggerEnrage();
        }
    }

    private void triggerEnrage() {
        isEnraged = true;
        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.5f);
        equipEnragedArmor(entity.getEquipment());
        onEnrage(entity);
    }

    private void equipEnragedArmor(EntityEquipment equipment) {
        if (equipment == null) return;

        equipment.setBoots(new ItemBuilder(Material.IRON_BOOTS).setUnbreakable(true).toItemStack());
        equipment.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).setUnbreakable(true).toItemStack());
        equipment.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
            .addEnchantment(Enchantment.PROTECTION, 1)
            .setLeatherColor("#B74355")
            .setUnbreakable(true)
            .toItemStack());
        equipment.setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
            .addEnchantment(Enchantment.PROTECTION, 1)
            .setLeatherColor("#B53C45")
            .setUnbreakable(true)
            .toItemStack());
    }

    private void playEnrageParticles() {
        entity.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, entity.getLocation().add(0, 1.8, 0), 1, 0.2, 0.1, 0.2, 0.0);
    }

    @Override
    public BlightedCreature clone() {
        BlightedCreature clone = (BlightedCreature) super.clone();
        clone.isEnraged = false;
        clone.particleTicks = 0;
        clone.setupEnrageTask();
        return clone;
    }
}
