package org.confluence.mod.common.entity.projectile.mana;

import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.*;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;

public class SkullProjectile extends AbstractManaProjectile {
    private static final WeaponTargeting TARGETING = new WeaponTargeting(TargetPolicy.ENEMY_ONLY);

    private static final int NO_TARGET_ID = -114514;
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(SkullProjectile.class, EntityDataSerializers.INT);
    private UUID targetUUID;
    private transient Entity target;

    public SkullProjectile(EntityType<? extends SkullProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
        withParticle(Confluence.asResource("skull_projectile_flame"));
    }

    public SkullProjectile(LivingEntity living) {
        this(ModEntities.SKULL.get(), living.level());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TARGET_ID, NO_TARGET_ID);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (level().isClientSide && DATA_TARGET_ID.equals(key)) {
            this.target = LibEntityUtils.getEntityOrPart(level(), entityData.get(DATA_TARGET_ID));
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();

        Entity current = getTarget();
        if (!TARGETING.canKeep(getOwner(), current)) {
            this.target = null;
            current = null;
            if (!level().isClientSide) {
                setTarget(LibEntityUtils.nearest(
                        TARGETING.hitTargets(level(), this, getOwner(), new AABB(blockPosition()).inflate(12.5), this::canHitEntity),
                        candidate -> true, Comparator.comparingDouble(this::distanceToSqr)));
                current = getTarget();
            }
        }
        if (current != null) {
            Vec3 direction = current.getBoundingBox().getCenter().subtract(position()).normalize();
            Vec3 velocity = getDeltaMovement().add(direction.scale(0.4375));
            if (velocity.lengthSqr() > 0.4375 * 0.4375) velocity = velocity.normalize().scale(0.4375);
            setDeltaMovement(velocity);
        }

        doSimpleMove();
        updateRotation();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (doPenetrateCheck(entity)) {
            doHurtAndKnockback(entity, 0.35, 0.1);
            doDiscardInMaxPenetrate(3);
        }
    }

    public void setTarget(@Nullable Entity target) {
        this.target = target;
        if (target == null) {
            this.targetUUID = null;
            entityData.set(DATA_TARGET_ID, NO_TARGET_ID);
        } else {
            this.targetUUID = target instanceof PartEntity<?> ? null : target.getUUID();
            entityData.set(DATA_TARGET_ID, target.getId());
        }
    }

    public @Nullable Entity getTarget() {
        if (target == null && level().isClientSide) {
            target = LibEntityUtils.getEntityOrPart(level(), entityData.get(DATA_TARGET_ID));
        }
        if (target == null && targetUUID != null && level() instanceof ServerLevel level) {
            Entity entity = level.getEntity(targetUUID);
            if (entity != null) {
                this.target = entity;
                entityData.set(DATA_TARGET_ID, entity.getId());
            }
        }
        return target;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("TargetUUID")) {
            targetUUID = compound.getUUID("TargetUUID");
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
