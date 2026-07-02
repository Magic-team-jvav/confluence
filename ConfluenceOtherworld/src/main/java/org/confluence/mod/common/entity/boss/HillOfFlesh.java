package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 肉丘——静止的地狱 Boss，拥有环形伤害区域、5 只眼睛 + 5 张嘴巴。
 * Phase2 (HP<50%) 时外圈扩大、攻击加速。
 */
public class HillOfFlesh extends BaseBoss {
    private static final float INNER_RADIUS = 14.0F;
    private static final float OUTER_RADIUS = 75.0F;
    private static final float INNER_DAMAGE = 40.0F;
    private static final float OUTER_DAMAGE = 40.0F;
    private static final int DAMAGE_INTERVAL = 32;
    private static final int PART_COUNT = 10;

    private final Entity[] parts = new Entity[PART_COUNT];
    private final Set<LivingEntity> innerEntities = new HashSet<>();
    private float currentOuterRadius = 10F;
    private boolean phase2;
    private int damageTimer;

    // Part offsets (eyes 0-4, mouths 5-9)
    private static final double[][] PART_OFFSETS = {
            {8, 13, 5}, {-8, 10, 8}, {7, 8, -7}, {0.5, 6.5, 8}, {-3, 5.5, -6}, // eyes
            {0, 11, 0}, {-6, 9, -3}, {8, 8, 5}, {-6.5, 4, 8.5}, {7, 3, -7}    // mouths
    };

    public HillOfFlesh(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        setNoGravity(true);
        this.xpReward = 5000;
    }

    public boolean isPhase2() { return phase2; }

    @Nullable
    public LivingEntity findTargetForPart(Vec3 partPos) {
        float r = currentOuterRadius;
        List<LivingEntity> candidates = level().getEntitiesOfClass(LivingEntity.class,
                AABB.ofSize(position(), r * 2, 100, r * 2),
                e -> e.isAlive() && canAttack(e)
                        && e.distanceToSqr(position().add(0, 10, 0)) < r * r * 1.44
                        && e.position().subtract(position()).horizontalDistanceSqr() <= r * r);
        // Prefer players
        for (LivingEntity e : candidates) {
            if (e instanceof Player && e.canBeSeenAsEnemy()) return e;
        }
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }

    // === Attributes ===

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 1500.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.ARMOR, 15.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 0.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(100); // passiive — parts do the work
            }
        };
    }

    // === Parts ===

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) spawnParts();
    }

    private void spawnParts() {
        if (!(level() instanceof ServerLevel sl)) return;
        for (int i = 0; i < PART_COUNT; i++) {
            boolean isEye = i < 5;
            Entity part = isEye
                    ? BossEntities.HILL_OF_FLESH_EYE.get().create(level())
                    : BossEntities.HILL_OF_FLESH_MOUTH.get().create(level());
            if (part != null) {
                double[] off = PART_OFFSETS[i];
                part.setPos(position().add(off[0], off[1], off[2]));
                if (part instanceof HillOfFleshEye eye) eye.setMaster(this);
                else if (part instanceof HillOfFleshMouth mouth) mouth.setMaster(this);
                sl.addFreshEntity(part);
                addSubEntity(part);
                parts[i] = part;
            }
        }
    }

    private void tickParts() {
        for (int i = 0; i < PART_COUNT; i++) {
            if (parts[i] == null || !parts[i].isAlive()) continue;
            double[] off = PART_OFFSETS[i];
            Vec3 dir = new Vec3(off[0], off[1], off[2]).yRot(-getYRot() * Mth.DEG_TO_RAD);
            parts[i].setPos(getX() + dir.x, getY() + dir.y, getZ() + dir.z);
        }
    }

    // === Tick ===

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            // Spawn animation: expand outer radius
            if (tickCount < 75) {
                currentOuterRadius = 10F + (OUTER_RADIUS - 10F) * (tickCount / 75F);
            }

            // Radius damage zones
            damageTimer++;
            if (damageTimer >= DAMAGE_INTERVAL) {
                damageTimer = 0;
                applyRadiusDamage();
            }

            // Phase transition
            if (!phase2 && getHealth() / getMaxHealth() < 0.5f) {
                phase2 = true;
            }

            // Track nearby entities
            if (tickCount % 64 == 0) {
                trackNearbyEntities();
            }

            tickParts();

            // Target for parts
            if (tickCount % 30 == 0) {
                Player nearest = level().getNearestPlayer(this, OUTER_RADIUS);
                if (nearest != null) setTarget(nearest);
            }
        }
    }

    private void applyRadiusDamage() {
        if (!(level() instanceof ServerLevel)) return;
        float inner = INNER_RADIUS;
        float outer = currentOuterRadius;
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                AABB.ofSize(position(), outer * 2, 100, outer * 2))) {
            if (!canAttack(e)) continue;
            double distSq = e.position().subtract(position()).horizontalDistanceSqr();
            if (distSq < (inner - 5) * (inner - 5)) {
                e.hurt(damageSources().magic(), INNER_DAMAGE);
            } else if (distSq < inner * inner) {
                e.hurt(damageSources().magic(), INNER_DAMAGE * 0.5f);
            } else if (distSq < outer * outer) {
                e.hurt(damageSources().magic(), OUTER_DAMAGE * 0.25f);
            }
            innerEntities.add(e);
        }
        innerEntities.removeIf(e -> !e.isAlive());
    }

    private void trackNearbyEntities() {
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                AABB.ofSize(position(), currentOuterRadius * 2.4, 100, currentOuterRadius * 2.4))) {
            if (canAttack(e) || innerEntities.contains(e)) {
                innerEntities.add(e);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Must be inside to deal full damage
        if (source.getEntity() != null && !innerEntities.contains(source.getEntity())) {
            return false;
        }
        return super.hurt(source, amount * 0.5f);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        for (Entity part : parts) if (part != null && part.isAlive()) part.discard();
    }

    @Override public boolean isPushable() { return false; }
    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override protected boolean shouldDiscardWhenNoTarget() { return false; }
    @Override public void remove(RemovalReason reason) {
        super.remove(reason);
        for (Entity part : parts) if (part != null && part.isAlive()) part.discard();
    }
}
