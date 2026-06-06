package fr.moussax.blightedMC.engine.entities.affixes;

import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.components.EntityComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public final class ChillingAffix implements EntityComponent {

    private static final double RADIUS = 6.0;
    private int tickCounter = 0;

    @Override
    public String getId() { return "AFFIX_CHILLING"; }

    @Override
    public void onInit(LivingEntity entity) {}

    @Override
    public void onDestroy(LivingEntity entity) {}

    @Override
    public void onTick(BlightedEntity owner) {
        tickCounter++;
        LivingEntity entity = owner.getEntity();

        drawFrostVortex(entity.getLocation(), tickCounter * 0.3);

        if (tickCounter >= 8) {
            tickCounter = 0;
            applyChilling(entity);
        }
    }

    private void drawFrostVortex(Location center, double rotation) {
        for (int i = 0; i < 48; i++) {
            double angle = rotation + (i * Math.PI * 2 / 48);

            double x = Math.cos(angle) * RADIUS;
            double z = Math.sin(angle) * RADIUS;
            Particle.DustOptions iceDust = new Particle.DustOptions(Color.fromRGB(150, 230, 255), 1.0f);
            Objects.requireNonNull(center.getWorld()).spawnParticle(Particle.DUST, center.clone().add(x, 0.1, z), 1, 0, 0, 0, 0, iceDust);

            double hx = Math.cos(angle + 0.1) * (RADIUS - 0.2);
            double hz = Math.sin(angle + 0.1) * (RADIUS - 0.2);
            Particle.DustOptions hazeDust = new Particle.DustOptions(Color.fromRGB(200, 255, 255), 0.6f);
            center.getWorld().spawnParticle(Particle.DUST, center.clone().add(hx, 0.15, hz), 1, 0, 0, 0, 0, hazeDust);
        }

        double innerAngle = -rotation * 2.0;
        for (int i = 0; i < 4; i++) {
            double offset = i * (Math.PI / 2);
            double sX = Math.cos(innerAngle + offset) * (RADIUS * 0.4);
            double sZ = Math.sin(innerAngle + offset) * (RADIUS * 0.4);

            center.getWorld().spawnParticle(Particle.WHITE_ASH, center.clone().add(sX, 0.2, sZ), 2, 0.1, 0.6, 0.1, 0.02);
        }
    }

    private void applyChilling(LivingEntity entity) {
        boolean slowed = false;
        for (org.bukkit.entity.Entity nearby : entity.getNearbyEntities(RADIUS, 3.0, RADIUS)) {
            if (nearby instanceof Player player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1, false, false, true));
                slowed = true;
            }
        }

        if (slowed && tickCounter == 0) {
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_POWDER_SNOW_STEP, 0.3f, 0.8f);
        }
    }

    @Override
    public EntityComponent clone() {
        try {
            ChillingAffix clone = (ChillingAffix) super.clone();
            clone.tickCounter = 0;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
