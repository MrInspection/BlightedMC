package fr.moussax.blightedMC.game.entities.spawnable.blighted;

import fr.moussax.blightedMC.core.entities.EntityNameTag;
import fr.moussax.blightedMC.core.entities.spawnable.SpawnableEntity;
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

public sealed abstract class BlightedFaction extends SpawnableEntity
    permits BlightedBogged, BlightedDrowned, BlightedHusk, BlightedParched, BlightedPiglin,
    BlightedSkeleton, BlightedStray, BlightedWitherSkeleton, BlightedZombie, BlightedZombifiedPiglin {

    protected boolean isEnraged;
    private final double enrageThreshold = 0.50;
    private BukkitRunnable abilityRunnable;
    private int particleTicks = 0;

    protected BlightedFaction(String entityId, String name, int maxHealth, EntityType entityType) {
        super(entityId, name, maxHealth, entityType, 0.30);
        setNameTagType(EntityNameTag.BLIGHTED);

        armor = new ItemStack[]{
            new ItemStack(Material.AIR),
            new ItemStack(Material.AIR),
            new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor("#B584D4").toItemStack(),
            new ItemBuilder(Material.LEATHER_HELMET).setLeatherColor("#A565C9").toItemStack()
        };

        addRepeatingTask(() -> {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity == null || entity.isDead()) {
                        cancel();
                        return;
                    }

                    if (!isEnraged) {
                        checkHealthAndEnrage();
                        return;
                    }

                    if (++particleTicks >= 5) {
                        playEnrageParticles();
                        particleTicks = 0;
                    }
                }
            };
            abilityRunnable = task;
            return task;
        }, 1L, 1L);
    }

    protected abstract void onEnrage(LivingEntity entity);

    private void checkHealthAndEnrage() {
        if (entity == null || isEnraged) return;

        double maxHealth = Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).getValue();
        if (entity.getHealth() / maxHealth <= enrageThreshold) {
            triggerEnrage();
        }
    }

    public void triggerEnrage() {
        isEnraged = true;

        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.5f);
        equipEnragedArmor(entity.getEquipment());

        onEnrage(entity);
    }

    private void equipEnragedArmor(EntityEquipment equipment) {
        if (equipment == null) return;

        equipment.setBoots(new ItemBuilder(Material.IRON_BOOTS).setUnbreakable(true).toItemStack());
        equipment.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).setUnbreakable(true).toItemStack());
        equipment.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.PROTECTION, 1).setLeatherColor("#B74355").setUnbreakable(true).toItemStack());
        equipment.setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.PROTECTION, 1).setLeatherColor("#B53C45").setUnbreakable(true).toItemStack());
    }

    private void playEnrageParticles() {
        entity.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, entity.getLocation().add(0, 1.8, 0), 1, 0.2, 0.1, 0.2, 0.0);
    }

    @Override
    public SpawnableEntity clone() {
        BlightedFaction clone = (BlightedFaction) super.clone();
        clone.isEnraged = false;
        clone.particleTicks = 0;

        clone.addRepeatingTask(() -> {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (clone.entity == null || clone.entity.isDead()) {
                        cancel();
                        return;
                    }
                    if (!clone.isEnraged) {
                        clone.checkHealthAndEnrage();
                        return;
                    }

                    if (++clone.particleTicks >= 5) {
                        clone.playEnrageParticles();
                        clone.particleTicks = 0;
                    }
                }
            };
            clone.abilityRunnable = task;
            return task;
        }, 1L, 1L);
        return clone;
    }
}
