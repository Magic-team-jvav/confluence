package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;

/**
 * 骷髅追踪弹幕。
 *
 * <p>存档保存稳定 UUID，运行时再解析实体并同步数字 ID 给客户端。读取必须先完整校验当前格式，
 * 再原子替换 UUID、服务端缓存和同步 ID，避免同一实体对象重复读取时残留旧目标。1.20 不读取
 * 旧扁平 UUID。</p>
 */
public class SkullProjectile extends AbstractManaProjectile {
    private static final String RUNTIME_TAG = "ConfluenceSkullRuntime";
    private static final int RUNTIME_VERSION = 1;
    private static final int NO_TARGET_ID = -114514;
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(SkullProjectile.class, EntityDataSerializers.INT);
    private UUID targetUUID;
    private transient LivingEntity target;

    public SkullProjectile(EntityType<SkullProjectile> entityType, Level level) {
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
            Entity entity = level().getEntity(entityData.get(DATA_TARGET_ID));
            this.target = entity instanceof LivingEntity living ? living : null;
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
            Vec3 vec3 = getDeltaMovement().add(LibMathUtils.getVectorA2B(this, target).scale(0.4375));
            if (vec3.lengthSqr() > 0.4375 * 0.4375) {
                setDeltaMovement(vec3.normalize().scale(0.4375));
            }
        } else {
            setTarget(null);
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

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
        if (target == null) {
            this.targetUUID = null;
            entityData.set(DATA_TARGET_ID, NO_TARGET_ID);
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
                entityData.set(DATA_TARGET_ID, living.getId());
            }
        }
        return target;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (combatState().isInvalid()) {
            return;
        }
        if (!compound.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            combatState().invalidate("Missing or invalid skull projectile runtime state");
            return;
        }
        CompoundTag runtime = compound.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("HasTarget", Tag.TAG_BYTE)) {
            combatState().invalidate("Malformed skull projectile runtime state");
            return;
        }

        boolean hasTarget = runtime.getBoolean("HasTarget");
        UUID restoredTargetUuid = null;
        if (hasTarget) {
            if (!runtime.hasUUID("Target")) {
                combatState().invalidate("Skull projectile target UUID is missing or malformed");
                return;
            }
            restoredTargetUuid = runtime.getUUID("Target");
        } else if (runtime.contains("Target")) {
            combatState().invalidate("Skull projectile contains an unexpected target UUID");
            return;
        }

        target = null;
        targetUUID = restoredTargetUuid;
        entityData.set(DATA_TARGET_ID, NO_TARGET_ID);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putBoolean("HasTarget", targetUUID != null);
        if (targetUUID != null) {
            runtime.putUUID("Target", targetUUID);
        }
        compound.put(RUNTIME_TAG, runtime);
    }
}
