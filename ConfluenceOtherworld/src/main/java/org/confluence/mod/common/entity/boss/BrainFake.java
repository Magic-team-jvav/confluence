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
import software.bernie.geckolib.util.GeckoLibUtil;

/// 克苏鲁之脑进入第二阶段后生成的镜像幻象。
///
/// 幻象不是另一只 Boss，也不复制本体的行为树、生命值或奖励逻辑。服务端只维护三个
/// 不同槽位，分别把本体位置沿目标的 X 轴、Z 轴以及 X/Z 两轴进行镜像，从而与本体共同
/// 组成围绕目标的四个候选位置。幻象完全不可交互，并作为临时 Boss 部件随本体统一清理。
public class BrainFake extends BaseBossPart<BrainOfCthulhu> implements GeoEntity {
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
            discard();
            return;
        }
        if (level().isClientSide) {
            return;
        }

        Vec3 position = master.position();
        if (master.getTarget() != null && master.getTarget().isAlive()) {
            int index = getIllusionIndex();
            double x = (index & 1) != 0
                    ? master.getTarget().getX() * 2.0 - master.getX()
                    : master.getX();
            double z = (index & 2) != 0
                    ? master.getTarget().getZ() * 2.0 - master.getZ()
                    : master.getZ();
            position = new Vec3(x, master.getY(), z);
        }

        setDeltaMovement(Vec3.ZERO);
        setPos(position);
        setYRot(master.getYRot());
        setXRot(master.getXRot());
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
