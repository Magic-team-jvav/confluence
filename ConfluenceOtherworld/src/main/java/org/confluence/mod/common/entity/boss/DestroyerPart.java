package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.confluence.lib.api.entity.Boss;
import org.confluence.lib.common.entity.PartHitTarget;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.EnemyDamageRules;
import org.confluence.mod.common.init.ModSoundEvents;
import org.mesdag.particlestorm.api.ParticleEmitterAttachable;
import org.mesdag.particlestorm.data.component.EmitterRate;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.mixed.IPSAnimatableInstanceCache;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/// 由头部持有、定位和同步的碰撞部件，不加入世界实体列表，不独立 tick 或存档。
public final class DestroyerPart extends PartEntity<TheDestroyer> implements Boss.BossPart, PartHitTarget, GeoEntity {
    /// 同一部位两次火花之间的最短间隔，单位 tick。
    private static final int HURT_SPARK_INTERVAL = 3;
    /// 加速喷流所需的最低体节速度，单位格/tick；天空模式仍持续喷出完整四种粒子。
    private static final double EXHAUST_MIN_SPEED = 0.08;
    /// 连续两次更新的速度增量达到此值时喷火，单位格/tick²。
    private static final double EXHAUST_ACCELERATION = 0.015;
    /// 体节每 tick 转向超过此角度时喷火，单位度，不包含绕轴滚动。
    private static final double EXHAUST_TURN_DEGREES = 1.0;
    /// 加速或转向结束后保留喷火的时间，单位 tick，避免临界值处闪烁。
    private static final int EXHAUST_HOLD_TICKS = 3;
    /// 地面模式逐节出土、展开侧翼时的排气持续时间，单位 tick。
    private static final int VENT_OPEN_TICKS = 6;
    /// 巡航喷流相对加速、转向喷流的粒子数量比例；四种效果始终一起工作。
    private static final float CRUISE_EXHAUST_STRENGTH = 0.25F;
    private Vec3 previousMotion = Vec3.ZERO;
    private int lastPoseTick = Integer.MIN_VALUE;
    private int exhaustUntilTick = Integer.MIN_VALUE;
    private final int index;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private Vec3 contactSweepStart = Vec3.ZERO;
    private boolean open;
    private boolean exhaustInitialized;
    private float roll;
    private int lastSparkTick = -HURT_SPARK_INTERVAL;

    public DestroyerPart(TheDestroyer parent, int index) {
        super(parent);
        this.index = index;
        noPhysics = true;
        setNoGravity(true);
        refreshDimensions();
    }

    public void updatePose(Vec3 position, Vec3 forward, float roll, boolean open) {
        int elapsed = getParent().tickCount - lastPoseTick;
        Vec3 motion = elapsed > 0 && elapsed <= EXHAUST_HOLD_TICKS ? position.subtract(position()).scale(1.0 / elapsed) : Vec3.ZERO;
        double speed = motion.length();
        boolean accelerating = speed - previousMotion.length() >= EXHAUST_ACCELERATION * Math.max(1, elapsed);
        boolean turning = previousMotion.lengthSqr() > EXHAUST_MIN_SPEED * EXHAUST_MIN_SPEED
                && getLookAngle().dot(forward.normalize()) < Math.cos(EXHAUST_TURN_DEGREES * Mth.DEG_TO_RAD * Math.max(1, elapsed));
        if (open && speed >= EXHAUST_MIN_SPEED && (accelerating || turning))
            exhaustUntilTick = getParent().tickCount + EXHAUST_HOLD_TICKS;
        if (!open || speed < EXHAUST_MIN_SPEED) exhaustUntilTick = Integer.MIN_VALUE;
        if (open && !this.open && getParent().getMode() == TheDestroyer.Mode.GROUND)
            exhaustUntilTick = getParent().tickCount + VENT_OPEN_TICKS;
        previousMotion = motion;
        lastPoseTick = getParent().tickCount;
        contactSweepStart = position();
        xo = xOld = getX();
        yo = yOld = getY();
        zo = zOld = getZ();
        yRotO = getYRot();
        xRotO = getXRot();
        setPos(position);
        if (forward.lengthSqr() > 1.0E-10) {
            if (forward.horizontalDistanceSqr() > 1.0E-10) {
                setYRot((float) (Mth.atan2(-forward.x, forward.z) * Mth.RAD_TO_DEG));
            }
            setXRot((float) (-Mth.atan2(forward.y, forward.horizontalDistance()) * Mth.RAD_TO_DEG));
        }
        this.open = open;
        this.roll = roll;
        tickCount = getParent().tickCount;
    }

    public Vec3 getContactSweepStart() {return contactSweepStart;}

    public int getIndex() {return index;}

    public boolean isTail() {return index == TheDestroyer.SEGMENT_COUNT - 1;}

    public boolean isOpen() {return open;}

    public float getRoll() {return roll;}

    public boolean isMetal() {return getParent().isMetal();}

    @Override
    public Vec3 getLightProbePosition(float partialTick) {
        return getParent().getBody().getRenderPosition(index, partialTick).add(0, TheDestroyer.BODY_HEIGHT / 2.0, 0);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(TheDestroyer.BODY_WIDTH * TheDestroyer.HITBOX_SCALE,
                TheDestroyer.BODY_HEIGHT * TheDestroyer.HITBOX_SCALE);
    }

    /// 扩大受击箱时保持身体中心，避免只向上扩张造成模型与碰撞箱偏移。
    @Override
    protected AABB makeBoundingBox() {
        float scale = 1.0F;
        return super.makeBoundingBox().move(0, (TheDestroyer.BODY_HEIGHT * scale - getBbHeight()) / 2.0, 0);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide || !isAttackable() || amount <= 0 || isInvulnerableTo(source)
                || EnemyDamageRules.blocks(this, source)) return false;
        boolean result = getParent().hurtFromPart(this, source, amount);
        int age = getParent().tickCount;
        if (result && age - lastSparkTick >= HURT_SPARK_INTERVAL) {
            lastSparkTick = age;
            playSound(ModSoundEvents.METAL_HURT.get(), 1.0F, 1.0F);
            /// 负事件编号表示实际受击体节；事件仍通过已追踪的头部发送。
            level().broadcastEntityEvent(getParent(), (byte) -index);
        }
        return result;
    }

    public void emitHurtSparks() {
        MolangParticleEngine.INSTANCE._addEmitter(new ParticleEmitter(level(), getBoundingBox().getCenter(),
                Confluence.asResource("mechanical_boss_hurt_spark"),
                new MolangExp("variable.radius", Math.max(getBbWidth(), getBbHeight()) / 2.0F)));
    }

    @Override
    public boolean isAttackable() {return !isRemoved() && !getParent().isRemoved() && getParent().isAlive();}

    @Override
    public boolean isPickable() {return isAttackable();}

    @Override
    public boolean canBeHitByProjectile() {return isAttackable();}

    @Override
    public boolean is(Entity entity) {return this == entity || entity == getParent();}

    @Override
    public boolean fireImmune() {return true;}

    @Override
    public Entity encounterOwner() {return getParent();}

    @Override
    public Entity dedupeIdentity() {return getParent();}

    @Override
    public boolean acceptsDirectHit() {return isAttackable();}

    @Override
    public boolean shouldBeSaved() {return false;}

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        /// TODO 侧翼开合动画补齐后由 open 控制切换，滚动单独应用于身体纵轴。
        ((ParticleEmitterAttachable) (Object) this).getVariableTable().setValue("variable.destroyer_exhaust",
                instance -> open && getParent().isAlive() && getParent().tickCount <= exhaustUntilTick ? 1.0F : 0.0F);
        ((ParticleEmitterAttachable) (Object) this).getVariableTable().setValue("variable.destroyer_exhaust_strength",
                instance -> getParent().tickCount <= exhaustUntilTick ? 1.0F : CRUISE_EXHAUST_STRENGTH);
        RawAnimation fire = RawAnimation.begin().thenLoop("fire");
        controllers.add(new AnimationController<>(this, state -> {
            if (isTail() || index % 2 == 0) return PlayState.STOP;
            boolean boost = open && getParent().isAlive() && getParent().tickCount <= exhaustUntilTick;
            boolean emitting = open && getParent().isAlive()
                    && (getParent().getMode() == TheDestroyer.Mode.SKY || boost);
            /// 1.5.1 不会更新 inactive 发射器的寿命表达式；在动画回调中显式开关已绑定的发射器。
            for (var ids : IPSAnimatableInstanceCache.of(cache).particlestorm$getCachedId().values()) {
                for (int id : ids) {
                    ParticleEmitter emitter = MolangParticleEngine.getEmitter(id);
                    if (emitter != null && !emitter.isRemoved()) {
                        emitter.active = emitting && !getParent().isSolid(emitter.getPosition());
                        /// 1.5.1 的 steady 速率只在初始化时计算；重新应用现有组件以更新当前体节的喷流强度。
                        for (var component : emitter.getPreset().components)
                            if (component instanceof EmitterRate.Steady rate) rate.apply(emitter);
                    }
                }
            }
            exhaustInitialized |= emitting;
            return exhaustInitialized ? state.setAndContinue(fire) : PlayState.STOP;
        }));
    }
}
