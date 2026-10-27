package org.confluence.mod.common.entity.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoundEvents;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 毁灭者专用临时体节；共享本体生命，不参与世吞断节及其移动逻辑。
public final class DestroyerPart extends BaseBossPart<TheDestroyer> implements GeoEntity {
    private static final EntityDataAccessor<Integer> INDEX = SynchedEntityData.defineId(DestroyerPart.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(DestroyerPart.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> OPEN = SynchedEntityData.defineId(DestroyerPart.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> METAL = SynchedEntityData.defineId(DestroyerPart.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float previousRoll, displayedRoll;
    private long rollUpdateTick;
    /// 有效受击时通知客户端生成机械火花的实体事件编号。
    private static final byte HURT_SPARK_EVENT = 72;
    /// 同一部位两次火花之间的最短间隔，单位 tick。
    private static final int HURT_SPARK_INTERVAL = 3;
    private int lastSparkTick = -HURT_SPARK_INTERVAL;

    public DestroyerPart(EntityType<?> type, Level level) {super(type, level);}

    public void attach(TheDestroyer owner, int index, Vec3 position) {
        setPos(position);
        entityData.set(INDEX, index);
        bindTo(owner);
    }

    public void updatePose(Vec3 position, Vec3 forward, float roll, boolean open, boolean metal) {
        setPos(position);
        setYRot((float) (Mth.atan2(-forward.x, forward.z) * Mth.RAD_TO_DEG));
        setXRot((float) (-Mth.atan2(forward.y, forward.horizontalDistance()) * Mth.RAD_TO_DEG));
        entityData.set(ROLL, roll);
        entityData.set(OPEN, open);
        entityData.set(METAL, metal);
    }

    /// 网络角度可能使用不同的整周表示；俯仰也沿最短角差插值，避免局部体节绕远路翻转。
    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps, boolean teleport) {
        super.lerpTo(x, y, z, yaw, getXRot() + Mth.wrapDegrees(pitch - getXRot()), steps, teleport);
    }

    /// 被追踪的客户端体节必须持续消费位置插值，否则进入未加载区块后无法移回加载区。
    /// 只影响客户端实体 tick，不申请区块票据，也不改变服务端的加载范围。
    @Override
    public boolean isAlwaysTicking() {
        return level().isClientSide;
    }

    public int getIndex() {return entityData.get(INDEX);}

    public boolean isTail() {return getIndex() == TheDestroyer.SEGMENT_COUNT - 1;}

    public boolean isOpen() {return entityData.get(OPEN);}

    public boolean isMetal() {return entityData.get(METAL);}

    public float getRoll(float partialTick) {
        float progress = Mth.clamp(level().getGameTime() - rollUpdateTick + partialTick, 0, 1);
        return previousRoll + Mth.wrapDegrees(displayedRoll - previousRoll) * progress;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ROLL.equals(key)) {
            previousRoll = displayedRoll;
            displayedRoll = entityData.get(ROLL);
            rollUpdateTick = level().getGameTime();
        }
    }

    /// 在 Forge/NeoForge 的离开世界回调中处理，覆盖客户端直接 setRemoved 的卸载路径。
    @Override
    public void onRemovedFromWorld() {
        TheDestroyer owner = getOwner();
        if (owner != null) owner.removeSubEntity(this);
        super.onRemovedFromWorld();
    }

    @Override
    protected Class<TheDestroyer> getOwnerType() {return TheDestroyer.class;}

    @Override
    protected void tickPart(TheDestroyer owner) {}

    @Override
    protected void definePartSynchedData() {
        entityData.define(INDEX, 1);
        entityData.define(ROLL, 0.0F);
        entityData.define(OPEN, false);
        entityData.define(METAL, false);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = hurtOwnerAndPart(source, amount, 1.0F);
        if (result && tickCount - lastSparkTick >= HURT_SPARK_INTERVAL) {
            lastSparkTick = tickCount;
            playSound(ModSoundEvents.METAL_HURT.get(), 1.0F, 1.0F);
            level().broadcastEntityEvent(this, HURT_SPARK_EVENT);
        }
        return result;
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (level().isClientSide && event == HURT_SPARK_EVENT) {
            MolangParticleEngine.INSTANCE._addEmitter(new ParticleEmitter(level(), getBoundingBox().getCenter(),
                    Confluence.asResource("mechanical_boss_hurt_spark"),
                    new MolangExp("variable.radius", Math.max(getBbWidth(), getBbHeight()) / 2.0F)));
        } else super.handleEntityEvent(event);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        /// TODO 侧翼开合动画补齐后由 OPEN 控制切换，滚动单独应用于身体纵轴。
        RawAnimation fire = RawAnimation.begin().thenLoop("fire");
        controllers.add(new AnimationController<>(this, state -> {
            if (!isTail() && getIndex() % 2 == 1 && getIndex() < 10) {
                return state.setAndContinue(fire);
            }
            return PlayState.STOP;
        }));
    }
}
