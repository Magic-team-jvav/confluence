package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.jetbrains.annotations.Nullable;
import org.mesdag.particlestorm.PSGameClient;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.Comparator;
import java.util.UUID;

public class SkullProjectile extends AbstractManaProjectile {
    private ParticleEmitter trail;
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(SkullProjectile.class, EntityDataSerializers.INT);
    private UUID targetUUID;
    private transient LivingEntity target;

    public SkullProjectile(EntityType<SkullProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public SkullProjectile(LivingEntity living) {
        this(ModEntities.SKULL_PROJECTILE.get(), living.level());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder.define(DATA_TARGET_ID, -114514));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide && DATA_TARGET_ID.equals(key)) {
            Entity entity = level().getEntity(entityData.get(DATA_TARGET_ID));
            if (entity instanceof LivingEntity living) {
                this.target = living;
            }
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (getTarget() == null) {
            if (!level().isClientSide) {
                level().getEntitiesOfClass(LivingEntity.class, new AABB(blockPosition()).inflate(12.5), living -> living instanceof Enemy).stream()
                        .min(Comparator.comparingDouble(living -> living.distanceToSqr(this))).ifPresent(this::setTarget);
            }
        } else if (!target.isRemoved()) {
            Vec3 vec3 = getDeltaMovement().add(VectorUtils.getVectorA2B(this, target).scale(0.4375));
            if (vec3.lengthSqr() > 0.4375 * 0.4375) {
                setDeltaMovement(vec3.normalize().scale(0.4375));
            }
        } else {
            setTarget(null);
        }

        if (level().isClientSide) {
            if (trail == null || trail.isRemoved()) {
                this.trail = new ParticleEmitter(level(), position(), Confluence.asResource("skull_projectile_flame"));
                trail.attachEntity(this);
                PSGameClient.LOADER.addEmitter(trail, false);
            }
        }

        doSimpleMove();
        updateRotation();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0.35, 0.1);
            doDiscardInMaxPenetrate(3);
        }
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
        if (target == null) {
            this.targetUUID = null;
            entityData.set(DATA_TARGET_ID, -114514);
        } else {
            this.targetUUID = target.getUUID();
            entityData.set(DATA_TARGET_ID, target.getId());
        }
    }

    public @Nullable LivingEntity getTarget() {
        if (target == null && targetUUID != null && level() instanceof ServerLevel level) {
            Entity entity = level.getEntity(targetUUID);
            if (entity instanceof LivingEntity living) {
                this.target = living;
            }
        }
        return target;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("TargetUUID")) {
            this.targetUUID = compound.getUUID("TargetUUID");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (targetUUID != null) {
            compound.putUUID("TargetUUID", targetUUID);
        }
    }
}
