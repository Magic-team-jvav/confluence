package org.confluence.mod.common.entity.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 克苏鲁之脑进入第二阶段后生成的镜像幻象。
///
/// 幻象不是另一只 Boss，也不复制本体的行为树、生命值或奖励逻辑。服务端只维护三个
/// 不同槽位，把本体相对目标的水平向量分别旋转 90、180 和 270 度，从而与本体共同
/// 组成围绕目标的四个候选位置。幻象完全不可交互，并作为临时 Boss 部件随本体统一清理。
public class BrainFake extends BaseBossPart<BrainOfCthulhu> implements GeoEntity {
    private static final RawAnimation OPEN = RawAnimation.begin().thenLoop("open");
    private static final EntityDataAccessor<Integer> ILLUSION_INDEX = SynchedEntityData.defineId(BrainFake.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BrainFake(EntityType<?> type, Level level) {
        super(type, level);
    }

    /// 将幻象绑定到本体以及固定镜像槽位。
    ///
    /// @param master 克苏鲁之脑本体
    /// @param index  镜像槽位，合法范围为 1 到 3
    public void setMaster(BrainOfCthulhu master, int index) {
        setIllusionIndex(index);
        bindTo(master);
        master.bindIllusion(this);
    }

    public void setIllusionIndex(int index) {
        entityData.set(ILLUSION_INDEX, Mth.clamp(index, 1, 3));
    }

    public int getIllusionIndex() {
        return entityData.get(ILLUSION_INDEX);
    }

    /// 幻象随本体技能淡入淡出，并随本体损失生命逐渐变得更清晰。
    public float getFadeProgress(float partialTick) {
        BrainOfCthulhu master = getOwner();
        if (master == null) return 0.0F;
        float healthVisibility = 1.0F - master.getHealth() / master.getMaxHealth() * 0.5F;
        return Math.min(healthVisibility, master.getFadeProgress(partialTick));
    }

    /// 镜像轴与本体或另一个镜像重合时只绘制一个表面，避免两个半透明模型
    /// 在完全相同的深度上交替通过深度测试而闪烁。
    public boolean hasDistinctRenderPosition(float partialTick) {
        BrainOfCthulhu master = getOwner();
        if (master == null) return false;
        Vec3 renderPosition = getSmoothRenderPosition(partialTick);
        if (renderPosition.distanceToSqr(master.getPosition(partialTick)) < 1.0E-4) return false;
        for (net.minecraft.world.entity.Entity part : master.getSubEntities()) {
            if (part instanceof BrainFake other
                    && other != this
                    && other.getIllusionIndex() < getIllusionIndex()
                    && !other.isRemoved()
                    && renderPosition.distanceToSqr(other.getSmoothRenderPosition(partialTick)) < 1.0E-4) {
                return false;
            }
        }
        return true;
    }

    public Vec3 getSmoothRenderPosition(float partialTick) {
        BrainOfCthulhu master = getOwner();
        if (master == null) return getPosition(partialTick);
        net.minecraft.world.entity.Entity target = master.getSyncedVisualTarget();
        // 目标实体 ID 的同步可能晚于幻象生成包；这段窗口内服务端同步的幻象实体位置
        // 仍然有效。回退到本体位置会让去重逻辑把三只幻象全部当成重合表面而隐藏。
        if (target == null) return getPosition(partialTick);
        Vec3 masterPosition = master.getPosition(partialTick);
        Vec3 targetPosition = target.getPosition(partialTick);
        return calculateIllusionPosition(masterPosition, targetPosition, getIllusionIndex());
    }

    @Override
    protected void definePartSynchedData() {
        entityData.define(ILLUSION_INDEX, 1);
    }

    @Override
    protected Class<BrainOfCthulhu> getOwnerType() {
        return BrainOfCthulhu.class;
    }

    @Override
    protected void tickPart(BrainOfCthulhu master) {
        if (!master.isPhase2()) {
            // 客户端可能先收到幻象生成包、后一帧才收到本体的阶段数据。此时不能在
            // 客户端永久丢弃实体；是否清理幻象只由服务端的权威阶段状态决定。
            if (!level().isClientSide) discard();
            return;
        }
        if (level().isClientSide) {
            net.minecraft.world.entity.Entity target = master.getSyncedVisualTarget();
            if (target != null) {
                // 客户端实体坐标只接受服务端网络插值。渲染器会直接由本体和目标的
                // partialTick 位置计算视觉锚点；这里再次 setPos 会重置网络插值，
                // 与渲染修正互相争抢，表现为幻象逐 tick 卡顿或来回跳动。
                Vec3 renderPosition = getSmoothRenderPosition(1.0F);
                faceTarget(renderPosition, target.getEyePosition());
            }
            return;
        }

        Vec3 position = master.position();
        if (master.getTarget() != null && master.getTarget().isAlive()) {
            position = calculateIllusionPosition(master.position(), master.getTarget().position(),
                    getIllusionIndex());
        }

        setDeltaMovement(Vec3.ZERO);
        setPos(position);
        if (master.getTarget() != null && master.getTarget().isAlive()) {
            faceTarget(position, master.getTarget().getEyePosition());
        } else {
            setRot(master.getYRot(), master.getXRot());
        }
    }

    /// 三个槽位分别占据本体绕目标旋转 90、180 和 270 度的位置。
    ///
    /// 旧实现分别翻转 X/Z 分量；本体与目标处于同一轴线时会退化成两个重合位置，视觉上
    /// 最终只剩玩家身后一只幻象。等半径旋转既保留“四选一”的行为，也不会产生退化槽位。
    private static Vec3 calculateIllusionPosition(Vec3 masterPosition, Vec3 targetPosition, int index) {
        double offsetX = masterPosition.x - targetPosition.x;
        double offsetZ = masterPosition.z - targetPosition.z;
        if (offsetX * offsetX + offsetZ * offsetZ < 1.0D) {
            // 本体几乎与目标垂直重合时仍需给三个幻象稳定的可见半径。
            offsetX = 4.0D;
            offsetZ = 0.0D;
        }
        return switch (Mth.clamp(index, 1, 3)) {
            case 1 -> new Vec3(targetPosition.x - offsetZ, masterPosition.y,
                    targetPosition.z + offsetX);
            case 2 -> new Vec3(targetPosition.x - offsetX, masterPosition.y,
                    targetPosition.z - offsetZ);
            default -> new Vec3(targetPosition.x + offsetZ, masterPosition.y,
                    targetPosition.z - offsetX);
        };
    }

    private void faceTarget(Vec3 origin, Vec3 target) {
        Vec3 direction = target.subtract(origin);
        if (direction.lengthSqr() <= 1.0E-7) return;
        float yaw = (float) (Mth.atan2(direction.z, direction.x) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) (-Mth.atan2(direction.y, direction.horizontalDistance()) * Mth.RAD_TO_DEG);
        setRot(yaw, pitch);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        BrainOfCthulhu master = getOwner();
        if (master != null) {
            master.onIllusionRemoved(this);
        }
        super.remove(reason);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Brain", 0,
                state -> state.setAndContinue(OPEN)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
