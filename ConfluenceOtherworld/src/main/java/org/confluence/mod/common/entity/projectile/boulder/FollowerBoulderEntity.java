package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 在限定寿命内持续追踪同一目标的巨石。
 *
 * <p>运行时实体引用不能跨区块卸载保存，因此服务端存储目标 UUID 并在目标重新加载后懒解析。
 * 单独的追踪年龄延续原有 20 秒上限，不能因重载重新获得完整追踪时间。</p>
 */
public class FollowerBoulderEntity extends BoulderEntity {
    private static final String RUNTIME_TAG = "ConfluenceFollowerBoulderRuntime";
    private static final int RUNTIME_VERSION = 1;
    private static final int MAX_TRACKING_AGE = 20 * 20;

    /**
     * 已消耗的追踪寿命，与实体通用 tickCount 分开保存。
     */
    private int trackingAge;
    /**
     * 仅在目标当前已加载时缓存，不能直接作为持久状态。
     */
    private @Nullable Entity target;
    private @Nullable UUID targetUUID;
    private boolean invalidFollowerRuntimeState;

    public FollowerBoulderEntity(EntityType<FollowerBoulderEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.trackingAge = 0;
    }

    public FollowerBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.FOLLOWER_BOULDER.get(), level, pos, blockState);
        this.trackingAge = 0;
    }

    @Override
    public void tick() {
        if (invalidFollowerRuntimeState) {
            if (!level().isClientSide) {
                discard();
            }
            return;
        }
        super.tick();
    }

    @Override
    public void baseTick() {
        super.baseTick();
        Entity resolvedTarget = getTrackingTarget();
        if (resolvedTarget != null) {
            Vec3 vec3 = resolvedTarget.position().subtract(position()).normalize();
            vec3 = new Vec3(vec3.x, 0.0, vec3.z);
            setDeltaMovement(vec3.scale(speed / 1.75F));
            this.yRotO = getYRot();
            setYRot((float) (Mth.atan2(vec3.x, vec3.z) * Mth.RAD_TO_DEG));
            if (distanceTo(resolvedTarget) >= 30) onRemove();
        }
        if (trackingAge++ >= MAX_TRACKING_AGE) onRemove();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        onRemove();
    }

    @Override
    public void targetTo(@Nullable Entity entity) {
        if (entity != null) {
            target = entity;
            targetUUID = entity.getUUID();
        }
    }

    /**
     * 优先复用已加载目标；服务端找不到 UUID 时保留身份，等待目标区块稍后加载。
     */
    private @Nullable Entity getTrackingTarget() {
        if (target != null && (targetUUID == null || targetUUID.equals(target.getUUID()))) {
            if (targetUUID == null) targetUUID = target.getUUID();
            return target;
        }
        if (targetUUID != null && level() instanceof ServerLevel serverLevel) {
            Entity resolved = serverLevel.getEntity(targetUUID);
            if (resolved != null) {
                target = resolved;
                return resolved;
            }
        }
        return null;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        invalidFollowerRuntimeState = false;
        trackingAge = 0;
        target = null;
        targetUUID = null;

        if (!tag.contains(RUNTIME_TAG)) {
            // 新生成实体允许没有追踪状态；不读取 1.20 早期临时字段。
            return;
        }
        if (!tag.contains(RUNTIME_TAG, Tag.TAG_COMPOUND)) {
            invalidFollowerRuntimeState = true;
            return;
        }
        CompoundTag runtime = tag.getCompound(RUNTIME_TAG);
        if (!runtime.contains("Version", Tag.TAG_INT)
                || runtime.getInt("Version") != RUNTIME_VERSION
                || !runtime.contains("Age", Tag.TAG_INT)) {
            invalidFollowerRuntimeState = true;
            return;
        }

        int savedAge = runtime.getInt("Age");
        if (savedAge < 0 || savedAge > MAX_TRACKING_AGE) {
            invalidFollowerRuntimeState = true;
            return;
        }
        if (runtime.contains("Target") && !runtime.hasUUID("Target")) {
            invalidFollowerRuntimeState = true;
            return;
        }
        trackingAge = savedAge;
        targetUUID = runtime.hasUUID("Target") ? runtime.getUUID("Target") : null;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag runtime = new CompoundTag();
        runtime.putInt("Version", RUNTIME_VERSION);
        runtime.putInt("Age", trackingAge);
        Entity resolvedTarget = getTrackingTarget();
        UUID savedTarget = resolvedTarget == null ? targetUUID : resolvedTarget.getUUID();
        if (savedTarget != null) {
            runtime.putUUID("Target", savedTarget);
        }
        tag.put(RUNTIME_TAG, runtime);
    }
}
