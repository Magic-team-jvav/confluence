package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Optional;
import java.util.UUID;

/**
 * 双子魔眼共享的所有权、阶段和死亡回报基础实现。
 *
 * <p>具体攻击时序保留在激光眼和魔焰眼各自类中；只有两者真正相同的生命周期职责
 * 放在这里，避免以后修复重载或多人归属时只改到其中一只。</p>
 */
public abstract class AbstractTwinEye extends BaseFlyingMonster {
    private static final String TRANSFORMED_TAG = "Transformed";
    private static final String TRANSITION_TICKS_TAG = "TransitionTicks";
    /**
     * 1.21 侧由十刻变形前段和二十刻变形后段组成。
     */
    private static final int TRANSITION_DURATION_TICKS = 30;
    private static final RawAnimation PHASE_ONE =
            RawAnimation.begin().thenLoop("type_1");
    private static final RawAnimation PHASE_ONE_DASH =
            RawAnimation.begin().thenLoop("type_1_run");
    private static final RawAnimation TRANSFORM =
            RawAnimation.begin()
                    .thenPlay("switching")
                    .thenLoop("type_2");
    private static final RawAnimation PHASE_TWO =
            RawAnimation.begin().thenLoop("type_2");
    private static final RawAnimation PHASE_TWO_DASH =
            RawAnimation.begin().thenLoop("type_2_run");

    private static final EntityDataAccessor<Optional<UUID>>
            OWNER_UUID = SynchedEntityData.defineId(
            AbstractTwinEye.class,
            EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean>
            DATA_TRANSFORMED = SynchedEntityData.defineId(
            AbstractTwinEye.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean>
            DATA_DASHING = SynchedEntityData.defineId(
            AbstractTwinEye.class,
            EntityDataSerializers.BOOLEAN);

    private final BossOwnerTracker<TheTwins> ownerTracker =
            new BossOwnerTracker<>(TheTwins.class);
    private boolean reportedDeath;
    private boolean playedTransformAnimation;
    private int transitionTicks;

    protected AbstractTwinEye(
            EntityType<? extends BaseFlyingMonster> type,
            Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(OWNER_UUID, Optional.empty());
        entityData.define(DATA_TRANSFORMED, false);
        entityData.define(DATA_DASHING, false);
    }

    public final void setMaster(TheTwins master) {
        ownerTracker.bind(this, master);
        entityData.set(
                OWNER_UUID, Optional.of(master.getUUID()));
    }

    public final @Nullable TheTwins getMaster() {
        return ownerTracker.resolve(this);
    }

    public final @Nullable UUID getMasterUUID() {
        return entityData.get(OWNER_UUID).orElse(null);
    }

    public final boolean isOwnedBy(TheTwins boss) {
        return boss.getUUID().equals(getMasterUUID());
    }

    public final boolean isTransformed() {
        return entityData.get(DATA_TRANSFORMED);
    }

    @Override
    protected final BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                /*
                 * 两只眼睛各自维护明确战斗状态，通用行为树不得再并行覆盖冲刺或
                 * 弹幕速度。
                 */
                return new WaitAction(100);
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || isRemoved()) {
            return;
        }

        TheTwins master = getMaster();
        if (master == null || !master.isAlive()) {
            setTarget(null);
            return;
        }
        LivingEntity masterTarget = master.getTarget();
        if (masterTarget != null && masterTarget.isAlive()) {
            setTarget(masterTarget);
        }

        if (!isTransformed()
                && getHealth() < getMaxHealth() * 0.5F) {
            entityData.set(DATA_TRANSFORMED, true);
            transitionTicks = TRANSITION_DURATION_TICKS;
            onCombatProfileChanged();
        }
        if (transitionTicks > 0) {
            transitionTicks--;
            setDeltaMovement(Vec3.ZERO);
            entityData.set(DATA_DASHING, false);
            return;
        }
        tickTwinCombat(master);
        entityData.set(DATA_DASHING, isDashCombatState());
    }

    protected abstract void tickTwinCombat(TheTwins master);

    protected abstract boolean isRetinazer();

    /**
     * 返回服务端战斗状态是否正处于冲刺阶段。
     *
     * <p>战斗状态机仍由具体眼睛维护，这里只同步渲染所需的最小布尔状态，避免把整个
     * 攻击计时器暴露给客户端。</p>
     */
    protected abstract boolean isDashCombatState();

    protected void onCombatProfileChanged() {}

    /**
     * 双子魔眼共用阶段动画，但各自从自己的资源文件读取同名动画键。
     *
     * <p>半血后先完整播放一次变形，再进入二阶段循环；冲刺状态由服务端同步，确保
     * 多人客户端看到的动画与真实伤害窗口一致，而不是根据可能有插值误差的速度猜测。</p>
     */
    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "combat",
                5,
                state -> {
                    if (!isTransformed()) {
                        playedTransformAnimation = false;
                        return state.setAndContinue(
                                entityData.get(DATA_DASHING)
                                        ? PHASE_ONE_DASH : PHASE_ONE);
                    }
                    if (!playedTransformAnimation) {
                        playedTransformAnimation = true;
                        return state.setAndContinue(TRANSFORM);
                    }
                    return state.setAndContinue(
                            entityData.get(DATA_DASHING)
                                    ? PHASE_TWO_DASH : PHASE_TWO);
                }));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !(target instanceof AbstractTwinEye)
                && !(target instanceof TheTwins)
                && super.canAttack(target);
    }

    @Override
    public void die(DamageSource source) {
        reportDeath();
        super.die(source);
    }

    private void reportDeath() {
        if (level().isClientSide || reportedDeath) {
            return;
        }
        UUID ownerUUID = getMasterUUID();
        if (ownerUUID == null) {
            return;
        }
        reportedDeath = true;
        TheTwins master = getMaster();
        if (master != null) {
            if (!master.isRemovingSubEntities()) {
                master.onTwinDefeated(isRetinazer(), this);
            }
        } else if (level() instanceof ServerLevel serverLevel) {
            BossChildDeathLedger.record(
                    serverLevel, ownerUUID, getUUID());
        }
    }

    final void cancelPendingSpawn() {
        reportedDeath = true;
        ownerTracker.clear(this);
        entityData.set(OWNER_UUID, Optional.empty());
        discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ownerTracker.save(tag);
        tag.putBoolean(TRANSFORMED_TAG, isTransformed());
        tag.putInt(TRANSITION_TICKS_TAG, transitionTicks);
        saveTwinCombat(tag);
    }

    protected void saveTwinCombat(CompoundTag tag) {}

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ownerTracker.load(tag);
        entityData.set(
                OWNER_UUID,
                Optional.ofNullable(ownerTracker.getOwnerUUID()));
        entityData.set(
                DATA_TRANSFORMED,
                tag.getBoolean(TRANSFORMED_TAG));
        transitionTicks = Math.max(
                0, tag.getInt(TRANSITION_TICKS_TAG));
        reportedDeath = false;
        onCombatProfileChanged();
        loadTwinCombat(tag);
    }

    protected void loadTwinCombat(CompoundTag tag) {}

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy()) {
            reportDeath();
        }
        ownerTracker.unbind(this);
        super.remove(reason);
    }

    @Override
    public boolean causeFallDamage(
            float fallDistance,
            float multiplier,
            DamageSource source) {
        return false;
    }
}
