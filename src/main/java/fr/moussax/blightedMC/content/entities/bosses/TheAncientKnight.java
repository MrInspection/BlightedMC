package fr.moussax.blightedMC.content.entities.bosses;

import fr.moussax.blightedMC.BlightedMC;
import fr.moussax.blightedMC.engine.entities.BlightedEntity;
import fr.moussax.blightedMC.engine.entities.EntityImmunities;
import fr.moussax.blightedMC.engine.entities.immunity.DamageType;
import fr.moussax.blightedMC.engine.player.BlightedPlayer;
import fr.moussax.blightedMC.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@EntityImmunities({DamageType.PROJECTILE, DamageType.FALL})
public class TheAncientKnight extends BlightedEntity {

    private static final ItemStack RED_BOOTS = createRedBoots();
    private static final Particle.DustOptions VOID_PURPLE = new Particle.DustOptions(Color.fromRGB(150, 40, 240), 1.2f);
    private static final Particle.DustOptions RUNIC_CYAN = new Particle.DustOptions(Color.fromRGB(50, 220, 240), 1.0f);

    private final List<StabPlayer> activeStabs = new CopyOnWriteArrayList<>();

    private int currentPhase = 1;

    public TheAncientKnight() {
        super("The Ancient Knight", 350, 30, EntityType.ZOMBIE);
        addAttribute(Attribute.SCALE, 4.0);
        addAttribute(Attribute.SPAWN_REINFORCEMENTS, 0.0);
        setBoss(true);

        armor = new ItemStack[]{
                new ItemStack(Material.NETHERITE_BOOTS),
                new ItemStack(Material.NETHERITE_LEGGINGS),
                new ItemStack(Material.NETHERITE_CHESTPLATE),
                new ItemStack(Material.NETHERITE_HELMET)
        };

        itemInMainHand = new ItemBuilder(Material.NETHERITE_SWORD).addEnchantmentGlint().toItemStack();
    }

    private static ItemStack createRedBoots() {
        return new ItemBuilder(Material.LEATHER_BOOTS)
                .setLeatherColor("#DC1414")
                .setUnbreakable(true)
                .toItemStack();
    }

    public static Location calculateGiantAnchor(Location visualLoc, float yaw, boolean inverted, double scale) {
        double rad = Math.toRadians(yaw);
        Vector forward = new Vector(-Math.sin(rad), 0, Math.cos(rad));
        Vector right = new Vector(-Math.cos(rad), 0, -Math.sin(rad));

        Location anchor = visualLoc.clone();
        anchor.setYaw(yaw);
        anchor.setPitch(0);

        if (inverted) {
            Vector offset = right.clone().multiply(-1.85 * scale).add(forward.clone().multiply(0.4 * scale));
            return anchor.subtract(offset).subtract(0, 2.5 * scale, 0); // ponytail:
        } else {
            Vector offset = right.clone().multiply(1.85 * scale).add(forward.clone().multiply(0.4 * scale));
            return anchor.subtract(offset).subtract(0, 7.8 * scale, 0); // ponytail:
        }
    }

    @Override
    protected void onDefineBehavior() {
        registerPhase(1.0, () -> {
            currentPhase = 1;
            setMainHandEquipped(true);
            addPhaseAbility(80L, 180L, this::stabTargetPlayers);
            addPhaseAbility(20L, 30L, () -> meleeAttackNearestPlayer(5.0));
        });

        registerPhase(0.66, () -> {
            setMainHandEquipped(true);
            addPhaseAbility(120L, 220L, this::executeStomp);
            addPhaseAbility(160L, 260L, this::executeSwordThrow);
            addPhaseAbility(20L, 30L, () -> meleeAttackNearestPlayer(5.0));
        });

        registerPhase(0.33, () -> {
            setMainHandEquipped(true);
            addPhaseAbility(100L, 220L, this::executeBladenado);
            addPhaseAbility(180L, 280L, this::executeSwordThrow);
            addPhaseAbility(20L, 30L, () -> meleeAttackNearestPlayer(5.0));
        });
    }

    @Override
    protected long onPhaseTransition(double healthThreshold) {
        if (healthThreshold == 1.0) return 0L;

        if (healthThreshold == 0.66) {
            currentPhase = 2;
            setBossBarAppearance(BarColor.YELLOW, BarStyle.SEGMENTED_6);
        } else if (healthThreshold == 0.33) {
            currentPhase = 3;
            setBossBarAppearance(BarColor.RED, BarStyle.SEGMENTED_6);
        }

        if (entity instanceof Mob mob) {
            mob.setAI(false);
            mob.setTarget(null);
        }

        Location center = entity.getLocation().clone();
        World world = Objects.requireNonNull(center.getWorld());

        new BukkitRunnable() {
            private int ticks = 0;

            @Override
            public void run() {
                if (!isAlive()) {
                    cancel();
                    return;
                }

                ticks++;
                if (ticks <= 45) {
                    Location hoverLoc = center.clone().add(0, Math.sin(ticks * 0.1) * 1.5 + 1.5, 0);
                    hoverLoc.setYaw((hoverLoc.getYaw() + ticks * 4) % 360);
                    entity.teleport(hoverLoc);

                    double radius = Math.max(0.5, 5.0 - (ticks * 0.1));
                    for (int i = 0; i < 8; i++) {
                        double angle = (ticks * 0.15) + (i * (2 * Math.PI / 8));
                        Location pLoc = hoverLoc.clone().add(radius * Math.cos(angle), 1.2, radius * Math.sin(angle));
                        world.spawnParticle(Particle.DUST, pLoc, 1, 0.0, 0.0, 0.0, 0.0, VOID_PURPLE);
                        world.spawnParticle(Particle.ENCHANT, pLoc, 1, 0.0, 0.0, 0.0, 0.5);
                    }

                    if (ticks % 10 == 0) {
                        world.playSound(hoverLoc, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.5f, 0.6f + (ticks * 0.02f));
                    }
                } else if (ticks == 46) {
                    Location blastLoc = entity.getLocation();
                    world.playSound(blastLoc, Sound.ENTITY_WITHER_SPAWN, 1.8f, 0.9f);
                    world.playSound(blastLoc, Sound.ITEM_TRIDENT_THUNDER, 2.0f, 0.8f);

                    world.spawnParticle(Particle.FLASH, blastLoc.clone().add(0, 1.5, 0), 2);
                    world.spawnParticle(Particle.SOUL, blastLoc.clone().add(0, 1.5, 0), 80, 2.0, 1.5, 2.0, 0.15);

                    damageAndKnockbackNearbyPlayers(blastLoc, 14.0, 0.0, 1.8, 0.5);
                    for (Player player : getNearbyPlayers(blastLoc, 14.0)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, false, true));
                    }
                } else if (ticks >= 60) {
                    cancel();
                    if (entity instanceof Mob mob) mob.setAI(true);
                }
            }
        }.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);

        return 60L;
    }

    private void stabTargetPlayers() {
        if (currentPhase != 1 || isPerformingAbility()) return;
        Player target = getNearestPlayer(20);
        if (target == null) return;

        BlightedPlayer blightedTarget = BlightedPlayer.getBlightedPlayer(target);
        if (blightedTarget == null) return;

        setPerformingAbility(true);
        if (entity instanceof Mob mob) mob.swingMainHand();

        activeStabs.add(new StabPlayer(blightedTarget, this));
        addPhaseDelayedAction(15L, () -> setPerformingAbility(false));
    }

    private void executeStomp() {
        if (isPerformingAbility()) return;
        setPerformingAbility(true);

        setBootsEquipped(RED_BOOTS);
        if (entity instanceof Mob mob) mob.setAI(false);

        Location startLocation = entity.getLocation().clone();
        World initialWorld = entity.getWorld();
        initialWorld.playSound(startLocation, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.6f, 0.6f);
        initialWorld.playSound(startLocation, Sound.BLOCK_ANVIL_LAND, 1.2f, 1.4f);

        new BukkitRunnable() {
            private int ticks = 0;

            @Override
            public void run() {
                if (!isAlive()) {
                    cleanup();
                    return;
                }

                ticks++;
                World world = entity.getWorld();

                if (ticks <= 14) {
                    double riseProgress = ticks / 14.0;
                    double yOffset = Math.sin(riseProgress * (Math.PI / 2.0)) * 6.0;
                    Location currentLoc = startLocation.clone().add(0, yOffset, 0);
                    entity.teleport(currentLoc);

                    world.spawnParticle(Particle.DUST, currentLoc.clone().add(0, 0.5, 0), 4, 0.3, 0.1, 0.3, 0.0, VOID_PURPLE);
                    world.spawnParticle(Particle.SOUL_FIRE_FLAME, currentLoc.clone().add(0, 0.2, 0), 2, 0.2, 0.1, 0.2, 0.02);

                    if (ticks == 14) {
                        world.playSound(currentLoc, Sound.ENTITY_GHAST_SHOOT, 1.5f, 0.5f);
                    }
                } else if (ticks <= 22) {
                    double plungeProgress = (ticks - 14) / 8.0;
                    double yOffset = 6.0 * (1.0 - (plungeProgress * plungeProgress));
                    Location currentLocation = startLocation.clone().add(0, Math.max(0, yOffset), 0);
                    entity.teleport(currentLocation);

                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, currentLocation, 6, 0.3, 0.5, 0.3, 0.04);
                    world.spawnParticle(Particle.CRIT, currentLocation.clone().add(0, 1.0, 0), 4, 0.2, 0.4, 0.2, 0.05);
                } else {
                    entity.teleport(startLocation);
                    cleanup();
                    detonateEarthquake();
                }
            }

            private void cleanup() {
                cancel();
                setBootsEquipped(armor != null && armor.length > 0 ? armor[0] : new ItemStack(Material.NETHERITE_BOOTS));
                if (entity instanceof Mob mob) mob.setAI(true);
                setPerformingAbility(false);
            }
        }.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
    }

    private void detonateEarthquake() {
        Location center = entity.getLocation().clone();
        World world = Objects.requireNonNull(center.getWorld());

        world.playSound(center, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.8f, 0.5f);
        world.playSound(center, Sound.ENTITY_IRON_GOLEM_HURT, 2.5f, 0.5f);
        world.playSound(center, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.5f, 0.7f);

        new BukkitRunnable() {
            private double currentRadius = 1.5;
            private final double maxRadius = 13.0;

            @Override
            public void run() {
                if (currentRadius > maxRadius || !isAlive()) {
                    cancel();
                    return;
                }

                int points = (int) (currentRadius * 14);
                for (int i = 0; i < points; i++) {
                    double angle = (2 * Math.PI / points) * i;
                    double x = currentRadius * Math.cos(angle);
                    double z = currentRadius * Math.sin(angle);
                    Location ringLoc = center.clone().add(x, 0, z);

                    Block highestBlock = world.getHighestBlockAt(ringLoc);
                    Location blockSurface = highestBlock.getLocation().add(0.5, 1.0, 0.5);

                    world.spawnParticle(Particle.BLOCK, blockSurface, 3, 0.2, 0.3, 0.2, highestBlock.getBlockData());
                    world.spawnParticle(Particle.DUST, blockSurface.clone().add(0, 0.2, 0), 1, 0.0, 0.0, 0.0, 0.0, RUNIC_CYAN);
                }

                for (Entity nearby : world.getNearbyEntities(center, currentRadius + 1.2, 3.5, currentRadius + 1.2)) {
                    if (nearby.equals(entity)) continue;
                    if (nearby instanceof Player player && player.getGameMode() == GameMode.SURVIVAL) {
                        double distance = player.getLocation().distance(center);
                        if (Math.abs(distance - currentRadius) <= 1.8) {
                            player.damage(22, entity);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 255, false, false, true));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 80, 255, false, false, true)); // ponytail: kept
                            player.setVelocity(new Vector(0, 0.6, 0));
                        }
                    }
                }

                currentRadius += 1.8;
            }
        }.runTaskTimer(BlightedMC.getInstance(), 1L, 2L);
    }

    private void executeSwordThrow() {
        if (isPerformingAbility()) return;
        List<Player> nearby = getNearbyPlayers(26.0);
        if (nearby.isEmpty()) return;

        Player target = nearby.stream().max(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(entity.getLocation())))
                .orElse(nearby.getFirst());

        setPerformingAbility(true);

        Location bossLoc = entity.getLocation();
        Vector toTarget = target.getLocation().toVector().subtract(bossLoc.toVector()).setY(0);
        if (toTarget.lengthSquared() > 0.001) {
            float throwYaw = (float) Math.toDegrees(-Math.atan2(toTarget.getX(), toTarget.getZ()));
            bossLoc.setYaw(throwYaw);
            entity.teleport(bossLoc);
        }

        World world = entity.getWorld();
        world.playSound(bossLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.5f, 0.6f);
        world.playSound(bossLoc, Sound.ITEM_TRIDENT_THROW, 1.2f, 0.7f);

        new BukkitRunnable() {
            private int tick = 0;
            private Giant thrownSword = null;
            private Location handOrigin;
            private Location targetApex;
            private boolean pinned = false;
            private int pinTicks = 0;
            private int returnStep = 0;
            private final int totalReturnSteps = 12;

            @Override
            public void run() {
                if (!isAlive()) {
                    cleanup();
                    return;
                }

                tick++;

                if (tick <= 6) {
                    if (tick == 1 && entity instanceof Mob mob) mob.swingMainHand();
                    world.spawnParticle(Particle.DUST, entity.getEyeLocation().add(0, 0.5, 0), 3, 0.2, 0.2, 0.2, 0.0, VOID_PURPLE);
                    return;
                }

                if (tick == 7) {
                    setMainHandEquipped(false);

                    double yawRad = Math.toRadians(entity.getLocation().getYaw());
                    Vector rightDir = new Vector(-Math.cos(yawRad), 0, -Math.sin(yawRad)).normalize();
                    handOrigin = entity.getEyeLocation().clone().add(rightDir.multiply(2.2)).subtract(0, 1.5, 0);

                    Location spawnLoc = calculateGiantAnchor(handOrigin, entity.getLocation().getYaw(), true, 0.85);
                    thrownSword = spawnInvertedSword(spawnLoc, 0.85);

                    targetApex = target.isOnline() ? target.getLocation().clone().add(0, 0.5, 0) : entity.getLocation().add(entity.getLocation().getDirection().multiply(12));
                    world.playSound(handOrigin, Sound.ITEM_TRIDENT_RIPTIDE_2, 1.8f, 0.9f);
                    return;
                }

                if (thrownSword == null || thrownSword.isDead()) {
                    cleanup();
                    return;
                }

                int flightTick = tick - 7;

                if (flightTick <= 10 && !pinned) {
                    double progress = flightTick / 10.0;
                    double arcY = Math.sin(progress * Math.PI) * 1.2;
                    Location currentLoc = handOrigin.clone().add(targetApex.toVector().subtract(handOrigin.toVector()).multiply(progress)).add(0, arcY, 0);

                    float spinYaw = (float) ((flightTick * 72) % 360);
                    thrownSword.teleport(calculateGiantAnchor(currentLoc, spinYaw, true, 0.85));

                    world.spawnParticle(Particle.SWEEP_ATTACK, currentLoc, 1, 0.0, 0.0, 0.0, 0.0);
                    world.spawnParticle(Particle.CRIT, currentLoc, 3, 0.15, 0.15, 0.15, 0.03);

                    for (Player player : getNearbyPlayers(26)) {
                        if (player.getLocation().distanceSquared(currentLoc) <= 6.0) {
                            player.damage(20, entity);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.2f, 0.8f);
                        }
                    }

                    if (flightTick == 10) {
                        pinned = true;
                        world.playSound(targetApex, Sound.BLOCK_ANVIL_LAND, 1.4f, 0.6f);
                        world.playSound(targetApex, Sound.BLOCK_STONE_BREAK, 1.8f, 0.5f);
                        world.spawnParticle(Particle.BLOCK, targetApex, 16, 0.4, 0.3, 0.4, world.getBlockAt(targetApex.clone().subtract(0, 1, 0)).getBlockData());
                        world.spawnParticle(Particle.DUST, targetApex.clone().add(0, 0.5, 0), 8, 0.3, 0.3, 0.3, 0.0, VOID_PURPLE);
                    }
                    return;
                }

                if (pinned && pinTicks < 12) {
                    pinTicks++;
                    Location pinLoc = targetApex.clone().add((Math.random() - 0.5) * 0.06, 0, (Math.random() - 0.5) * 0.06);
                    thrownSword.teleport(calculateGiantAnchor(pinLoc, entity.getLocation().getYaw(), true, 0.85));
                    world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, targetApex, 1, 0.05, 0.1, 0.05, 0.01);

                    if (pinTicks == 10) {
                        world.playSound(entity.getLocation(), Sound.BLOCK_CHAIN_FALL, 1.5f, 1.4f);
                    }
                    return;
                }

                returnStep++;
                double returnProgress = (double) returnStep / totalReturnSteps;

                double currentYawRad = Math.toRadians(entity.getLocation().getYaw());
                Vector dynamicRight = new Vector(-Math.cos(currentYawRad), 0, -Math.sin(currentYawRad)).normalize();
                Location returnHandLoc = entity.getEyeLocation().clone().add(dynamicRight.multiply(2.2)).subtract(0, 1.5, 0);

                double returnArcY = Math.sin(returnProgress * Math.PI) * 1.8;
                Location currentReturnLoc = targetApex.clone().add(returnHandLoc.toVector().subtract(targetApex.toVector()).multiply(returnProgress)).add(0, returnArcY, 0);

                float spinYaw = (float) ((returnStep * 60) % 360);
                thrownSword.teleport(calculateGiantAnchor(currentReturnLoc, spinYaw, true, 0.85));

                world.spawnParticle(Particle.DUST, currentReturnLoc, 2, 0.0, 0.0, 0.0, 0.0, RUNIC_CYAN);

                for (Player player : getNearbyPlayers(26)) {
                    if (player.getLocation().distanceSquared(currentReturnLoc) <= 5.0) {
                        player.damage(12, entity);
                    }
                }

                if (returnStep >= totalReturnSteps) {
                    if (entity instanceof Mob mob) {
                        mob.swingMainHand();
                    }
                    world.playSound(entity.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1.5f, 1.0f);
                    cleanup();
                }
            }

            private void cleanup() {
                cancel();
                if (thrownSword != null && !thrownSword.isDead()) {
                    thrownSword.remove();
                }
                setMainHandEquipped(true);
                setPerformingAbility(false);
            }
        }.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
    }

    private void executeBladenado() {
        if (currentPhase != 3 || isPerformingAbility()) return;
        setPerformingAbility(true);

        Location startCenter = entity.getLocation().clone();
        int bladeCount = 8;
        List<Giant> tornadoSwords = new ArrayList<>();

        setMainHandEquipped(false);
        if (entity instanceof Mob mob) mob.setAI(false);

        for (int i = 0; i < bladeCount; i++) {
            double angle = i * (2 * Math.PI / bladeCount);
            Location spawnLoc = startCenter.clone().add(1.5 * Math.cos(angle), 2.2, 1.5 * Math.sin(angle));
            Giant sword = spawnInvertedSword(spawnLoc, 0.85);
            tornadoSwords.add(sword);
        }

        World world = Objects.requireNonNull(startCenter.getWorld());
        world.playSound(startCenter, Sound.ITEM_TRIDENT_RIPTIDE_3, 2.0f, 0.6f);

        new BukkitRunnable() {
            private int ticks = 0;
            private double spinAngle = 0;
            private Location currentCenter = startCenter.clone();

            @Override
            public void run() {
                if (!isAlive()) {
                    cleanup();
                    return;
                }

                if (ticks >= 140) {
                    cleanup();
                    return;
                }

                Location currentCenter = entity.getLocation();

                Player chaseTarget = getNearestPlayer(16.0);
                if (chaseTarget != null) {
                    Vector chaseVector = chaseTarget.getLocation().toVector().subtract(currentCenter.toVector()).setY(0);
                    if (chaseVector.lengthSquared() > 0.5) {
                        currentCenter.add(chaseVector.normalize().multiply(0.12));
                    }
                }

                currentCenter.setYaw((currentCenter.getYaw() + 25) % 360);
                entity.teleport(currentCenter);

                spinAngle += 0.22;
                double radius = 4.8;

                for (int i = 0; i < bladeCount; i++) {
                    double angle = spinAngle + (i * (2 * Math.PI / bladeCount));
                    double xOffset = radius * Math.cos(angle);
                    double zOffset = radius * Math.sin(angle);
                    double yWave = Math.sin(ticks * 0.25 + (i * (2 * Math.PI / bladeCount))) * 0.4;

                    Location visualBladeTarget = currentCenter.clone().add(xOffset, 2.2 + yWave, zOffset);
                    float tangentYaw = (float) Math.toDegrees(angle);

                    Location giantAnchor = calculateGiantAnchor(visualBladeTarget, tangentYaw, true, 0.85);
                    tornadoSwords.get(i).teleport(giantAnchor);
                    world.spawnParticle(Particle.SWEEP_ATTACK, visualBladeTarget, 1);
                    world.spawnParticle(Particle.DUST, visualBladeTarget.clone().add(0, 0.2, 0), 1, 0.0, 0.0, 0.0, 0.0, VOID_PURPLE);
                }

                world.spawnParticle(Particle.CLOUD, currentCenter.clone().add(0, 2.0, 0), 4, 1.5, 0.4, 1.5, 0.03);

                if (ticks % 3 == 0) {
                    if (entity instanceof Mob mob) {
                        mob.swingMainHand();
                    }
                    world.playSound(currentCenter, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.4f, 0.6f);
                    for (Player player : getNearbyPlayers(6.5)) {
                        player.damage(12, entity);
                        Vector knockback = player.getLocation().toVector().subtract(currentCenter.toVector()).normalize().multiply(0.6).setY(0.2);
                        player.setVelocity(knockback);
                    }
                }
                ticks++;
            }

            private void cleanup() {
                cancel();
                tornadoSwords.forEach(Giant::remove);
                setMainHandEquipped(true);
                if (entity instanceof Mob mob) mob.setAI(true);
                setPerformingAbility(false);
            }
        }.runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
    }

    @Override
    public void onDamageTaken(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    public Giant spawnInvertedSword(Location spawnLoc, double scale) {
        Location targetLoc = spawnLoc.clone();
        targetLoc.setPitch(0);
        targetLoc.setYaw(0);

        return Objects.requireNonNull(spawnLoc.getWorld()).spawn(targetLoc, Giant.class, g -> {
            configureSwordGiant(g, scale);
            g.setCustomName("Dinnerbone");
        });
    }

    private void configureSwordGiant(Giant giant, double scale) {
        giant.setAI(false);
        giant.setCustomNameVisible(false);
        giant.setInvisible(true);
        giant.setInvulnerable(true);
        giant.setSilent(true);
        giant.setGravity(false);
        giant.setCollidable(false);

        AttributeInstance scaleAttribute = giant.getAttribute(Attribute.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.setBaseValue(scale);
        }

        Objects.requireNonNull(giant.getEquipment())
                .setItemInMainHand(new ItemBuilder(Material.NETHERITE_SWORD).addEnchantmentGlint().toItemStack());
    }

    private void setBootsEquipped(ItemStack bootsItem) {
        if (entity == null || entity.getEquipment() == null) return;
        entity.getEquipment().setBoots(bootsItem);
    }

    @Override
    public String getEntityId() {
        return "DIAMOND_GIANT";
    }

    @Override
    public TheAncientKnight clone() {
        TheAncientKnight clone = (TheAncientKnight) super.clone();
        try {
            var activeStabsField = TheAncientKnight.class.getDeclaredField("activeStabs");
            activeStabsField.setAccessible(true);
            activeStabsField.set(clone, new CopyOnWriteArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone sword lists", e);
        }
        return clone;
    }

    @Override
    public LivingEntity spawn(Location location) {
        super.spawn(location);
        if (entity instanceof Ageable ageable) {
            ageable.setAdult();
        }
        return entity;
    }

    @Override
    public void onDeath(Location location) {
        stopAllStabs();
    }

    private void stopAllStabs() {
        for (StabPlayer stab : new ArrayList<>(activeStabs)) {
            try {
                stab.cancel();
            } catch (IllegalStateException ignored) {
            }
        }
        activeStabs.clear();
    }

    private static class StabPlayer extends BukkitRunnable {
        private final BlightedPlayer target;
        private final TheAncientKnight owner;
        private Location stabledLocation;
        private Giant swordEntity;
        private int tick = 0;
        private boolean returning = false;
        private int returnStep = 0;
        private final int totalReturnSteps = 12;
        private Location groundApex;

        StabPlayer(BlightedPlayer target, TheAncientKnight owner) {
            this.target = target;
            this.owner = owner;
            summonSwordEntity();
            runTaskTimer(BlightedMC.getInstance(), 1L, 1L);
        }

        private void summonSwordEntity() {
            owner.setMainHandEquipped(false);
            Location spawnLocation = target.getPlayer().getLocation().clone();
            spawnLocation.setPitch(0);
            spawnLocation.setYaw(0);
            spawnLocation.subtract(2, -4, 4);

            swordEntity = owner.spawnInvertedSword(spawnLocation, 1.0);
        }

        @Override
        public void run() {
            if (!owner.isAlive() || swordEntity == null || swordEntity.isDead()) {
                cancel();
                return;
            }

            if (tick == 0) {
                stabledLocation = target.getPlayer().getLocation();
                Objects.requireNonNull(stabledLocation.getWorld())
                        .playSound(stabledLocation, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.0f, 0.75f);
            }

            if (!returning) {
                if (tick < 90) {
                    if (target.getPlayer().isOnline()) {
                        stabledLocation = target.getPlayer().getLocation().clone();
                        trackSwordAbovePlayer();
                    }
                } else if (tick == 101) {
                    if (owner.getEntity() instanceof Mob mob) {
                        mob.swingMainHand();
                    }
                    plungeAndDamage();
                    groundApex = stabledLocation.clone().add(0, 0.5, 0);
                } else if (tick >= 115) {
                    returning = true;
                    if (owner.getEntity() != null) {
                        owner.getEntity().getWorld().playSound(owner.getEntity().getLocation(), Sound.BLOCK_CHAIN_FALL, 1.5f, 1.4f);
                    }
                }
            } else {
                executeReturnFlight();
            }

            tick++;
        }

        private void executeReturnFlight() {
            returnStep++;
            double progress = (double) returnStep / totalReturnSteps;

            LivingEntity bossEntity = owner.getEntity();
            if (bossEntity == null || !bossEntity.isValid()) {
                cancel();
                return;
            }

            double currentYawRad = Math.toRadians(bossEntity.getLocation().getYaw());
            Vector dynamicRight = new Vector(-Math.cos(currentYawRad), 0, -Math.sin(currentYawRad)).normalize();
            Location returnHandLoc = bossEntity.getEyeLocation().clone().add(dynamicRight.multiply(2.2)).subtract(0, 1.5, 0);

            double returnArcY = Math.sin(progress * Math.PI) * 1.8;
            Location currentReturnLoc = groundApex.clone().add(returnHandLoc.toVector().subtract(groundApex.toVector()).multiply(progress)).add(0, returnArcY, 0);

            float spinYaw = (float) ((returnStep * 60) % 360);
            swordEntity.teleport(TheAncientKnight.calculateGiantAnchor(currentReturnLoc, spinYaw, true, 0.85));

            World world = Objects.requireNonNull(currentReturnLoc.getWorld());
            world.spawnParticle(Particle.DUST, currentReturnLoc, 2, 0.0, 0.0, 0.0, 0.0, RUNIC_CYAN);

            for (Player player : owner.getNearbyPlayers(26)) {
                if (player.getLocation().distanceSquared(currentReturnLoc) <= 5.0) {
                    player.damage(12, bossEntity);
                }
            }

            if (returnStep >= totalReturnSteps) {
                if (bossEntity instanceof Mob mob) {
                    mob.swingMainHand();
                }
                world.playSound(bossEntity.getLocation(), Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1.5f, 1.0f);
                cancel();
            }
        }

        private void trackSwordAbovePlayer() {
            Location above = target.getPlayer().getLocation().clone();
            above.setPitch(0);
            above.setYaw(0);
            above.subtract(2, -4, 4);
            swordEntity.teleport(above);
        }

        private void plungeAndDamage() {
            Location plungeLocation = stabledLocation.clone();
            plungeLocation.setPitch(0);
            plungeLocation.setYaw(0);
            plungeLocation.subtract(2, 1, 4);
            swordEntity.teleport(plungeLocation);

            World world = Objects.requireNonNull(stabledLocation.getWorld());
            world.spawnParticle(Particle.EXPLOSION_EMITTER, stabledLocation, 1);
            world.playSound(stabledLocation, Sound.BLOCK_ANVIL_LAND, 1.0f, 0.5f);
            owner.damageNearbyPlayers(stabledLocation, 6.0, 16.0);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            if (swordEntity != null && !swordEntity.isDead()) swordEntity.remove();
            owner.activeStabs.remove(this);
            if (owner.activeStabs.isEmpty()) {
                owner.setMainHandEquipped(true);
            }
        }
    }
}
