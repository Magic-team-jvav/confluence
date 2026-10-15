package org.confluence.mod.common.entity.soul;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.mod.common.entity.projectile.DamageSettableProjectile;
import org.confluence.mod.common.entity.projectile.ProjectileHitRules;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.soul.skill.SoulSkillCastContext;
import org.confluence.mod.common.soul.skill.SoulSkillParameters;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpiritSurgeEntity extends DamageSettableProjectile {
    private static final EntityDataAccessor<Long> DATA_START_TIME = SynchedEntityData.defineId(SpiritSurgeEntity.class, EntityDataSerializers.LONG);
    private static final double FULL_TURN = Math.PI * 2.0;
    private final Set<UUID> hitTargets = new HashSet<>();
    private double previousRadius;

    public SpiritSurgeEntity(EntityType<? extends SpiritSurgeEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public SpiritSurgeEntity(SoulSkillCastContext context) {
        this(ModEntities.SPIRIT_SURGE.get(), context.player().level());
        setOwner(context.player());
        setDamage(context.damage());
        setPos(context.player().position());
        entityData.set(DATA_START_TIME, level().getGameTime());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_START_TIME, 0L);
    }

    @Override
    public void tick() {
        super.tick();
        long elapsed = Math.max(0L, level().getGameTime() - entityData.get(DATA_START_TIME));
        double radius = SoulSkillParameters.SPIRIT_SURGE_RADIUS * Math.min(1.0, (double) elapsed / SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS);
        if (level().isClientSide) {
            for (int index = 0; index < SoulSkillParameters.SPIRIT_SURGE_RING_PARTICLE_COUNT; index++) {
                double angle = FULL_TURN * index / SoulSkillParameters.SPIRIT_SURGE_RING_PARTICLE_COUNT;
                level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getX() + Math.cos(angle) * radius, getY(), getZ() + Math.sin(angle) * radius, 0.0, 0.0, 0.0);
            }
            return;
        }
        if (getOwner() == null || elapsed > SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS) {
            discard();
            return;
        }
        AABB bounds = new AABB(getX() - radius, getY() - SoulSkillParameters.SPIRIT_SURGE_VERTICAL_REACH, getZ() - radius,
                getX() + radius, getY() + SoulSkillParameters.SPIRIT_SURGE_VERTICAL_REACH, getZ() + radius);
        for (Entity target : level().getEntities(this, bounds, entity -> ProjectileHitRules.logicalLivingTarget(entity) != null && ProjectileHitRules.canHit(getOwner(), entity))) {
            AABB targetBounds = target.getBoundingBox();
            double closestX = Math.max(targetBounds.minX - getX(), Math.max(0.0, getX() - targetBounds.maxX));
            double closestZ = Math.max(targetBounds.minZ - getZ(), Math.max(0.0, getZ() - targetBounds.maxZ));
            double furthestX = Math.max(Math.abs(targetBounds.minX - getX()), Math.abs(targetBounds.maxX - getX()));
            double furthestZ = Math.max(Math.abs(targetBounds.minZ - getZ()), Math.abs(targetBounds.maxZ - getZ()));
            if (closestX * closestX + closestZ * closestZ <= radius * radius
                    && furthestX * furthestX + furthestZ * furthestZ >= previousRadius * previousRadius
                    && hitTargets.add(ProjectileHitRules.dedupeIdentity(target).getUUID())) {
                ProjectileHitRules.damageRecipient(target).hurt(getDamageSource(), getDamage());
            }
        }
        previousRadius = radius;
        if (elapsed >= SoulSkillParameters.SPIRIT_SURGE_DURATION_TICKS) {
            discard();
        }
    }

    @Override
    public float getCalculatedDamage() {
        return getDamage();
    }

    @Override
    public DamageSource getDamageSource() {
        return LibDamageTypes.of(level(), LibDamageTypes.SOUL, this, getOwner());
    }
}
