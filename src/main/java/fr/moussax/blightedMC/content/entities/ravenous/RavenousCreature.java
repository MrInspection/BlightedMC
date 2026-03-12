package fr.moussax.blightedMC.content.entities.ravenous;

import fr.moussax.blightedMC.engine.entities.spawnable.SpawnableEntity;
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
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Objects;

public sealed abstract class RavenousCreature extends SpawnableEntity
    permits RavenousBogged, RavenousDrowned, RavenousHusk, RavenousParched, RavenousPiglin,
    RavenousSkeleton, RavenousStray, RavenousWitherSkeleton, RavenousZombie, RavenousZombifiedPiglin {

    private static final double ENRAGE_THRESHOLD = 0.50;

    protected boolean isEnraged;
    private int particleTicks = 0;

    protected RavenousCreature(String entityId, String name, EntityType entityType) {
        super(entityId, name, 30, entityType, 0.05);
        setupDefaultArmor();
    }

    @Override
    protected void onDefineBehavior() {
        addAbility(1L, 1L, this::tickEnrage);
    }

    private void setupDefaultArmor() {
        armor = new ItemStack[]{
            new ItemStack(Material.AIR),
            new ItemStack(Material.AIR),
            new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor("#BF5985").toItemStack(),
            new ItemBuilder(Material.LEATHER_HELMET).setLeatherColor("#BF5985").toItemStack()
        };
    }

    private void tickEnrage() {
        if (!isEnraged) {
            checkHealthAndEnrage();
        } else if (++particleTicks >= 5) {
            playEnrageParticles();
            particleTicks = 0;
        }
    }

    protected abstract void onEnrage(LivingEntity entity);

    private void checkHealthAndEnrage() {
        double maxHealth = Objects.requireNonNull(
            entity.getAttribute(Attribute.MAX_HEALTH)
        ).getValue();

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

        equipment.setBoots(new ItemBuilder(Material.IRON_BOOTS)
            .addEnchantment(Enchantment.DEPTH_STRIDER, 4)
            .setUnbreakable(true)
            .toItemStack());
        equipment.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS)
            .setUnbreakable(true)
            .toItemStack());
        equipment.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
            .addEnchantment(Enchantment.PROTECTION, 1)
            .setLeatherColor("#BB4BB4")
            .setArmorTrim(TrimMaterial.REDSTONE, TrimPattern.FLOW)
            .setUnbreakable(true)
            .toItemStack());
        equipment.setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
            .addEnchantment(Enchantment.PROTECTION, 1)
            .setLeatherColor("#BB4BB4")
            .setArmorTrim(TrimMaterial.REDSTONE, TrimPattern.FLOW)
            .setUnbreakable(true)
            .toItemStack());
    }

    private void playEnrageParticles() {
        entity.getWorld().spawnParticle(
            Particle.ANGRY_VILLAGER,
            entity.getLocation().add(0, 1.8, 0),
            1, 0.2, 0.1, 0.2, 0.0
        );
    }

    @Override
    public RavenousCreature clone() {
        RavenousCreature clone = (RavenousCreature) super.clone();
        clone.isEnraged = false;
        clone.particleTicks = 0;
        return clone;
    }
}
