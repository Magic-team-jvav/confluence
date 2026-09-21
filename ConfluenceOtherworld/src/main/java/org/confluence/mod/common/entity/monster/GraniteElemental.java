package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.SpawnPlacementChecks;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyingPursuitAction;
import org.confluence.mod.common.entity.ai.bt.leaf.LookForwardWanderFlyAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 花岗精。
///
/// 普通状态下会持续追逐目标；专家难度且生命低于一半时，受到攻击有概率进入防御循环。防御循环包含
/// 进入动画、防御坠落和退出动画三个阶段。阶段由服务端推进并通过实体数据同步，客户端
/// 只负责选择对应动画，避免多人环境中各客户端自行推算阶段而产生视觉分歧。
///
/// 防御阶段会暂时恢复重力、停止横向移动并吸收普通伤害。花岗岩元素具有正常碰撞，不能像幽灵类生物一样
/// 穿墙，否则防御坠落会直接穿过地面。
public class GraniteElemental extends BaseFlyingMonster {
    private static final String DEFENSE_PHASE_TAG = "DefensePhase";
    private static final String DEFENSE_TICKS_TAG = "DefenseTicks";
    private static final EntityDataAccessor<Byte> DATA_DEFENSE_PHASE = SynchedEntityData.defineId(GraniteElemental.class, EntityDataSerializers.BYTE);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation TO_DEFENSE = RawAnimation.begin().thenPlayAndHold("to_defense");
    private static final RawAnimation DEFENSE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation FROM_DEFENSE = RawAnimation.begin().thenPlayAndHold("from_defense");

    /**
     * 同一洞穴群的水平密度检查半径，远处独立洞穴不共享数量上限。
     */
    private static final double LOCAL_POPULATION_RADIUS = 64.0;
    /**
     * 密度检查采用较短的垂直半径，避免上下相隔很远的洞穴互相占用名额。
     */
    private static final double LOCAL_POPULATION_HEIGHT = 32.0;

    private int defenseTicks;

    public GraniteElemental(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        setDiscardFriction(true);
        this.moveControl = new FlyingMoveControl(this, 180, true);
    }

    /// 花岗精只会在洞穴层生成；同一片活动区域最多存在一只，不限制远处独立花岗岩洞。
    public static boolean checkSpawn(EntityType<? extends Mob> type, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (!SpawnPlacementChecks.checkCaveMonsterSpawn(type, level, spawnType, pos, random))
            return false;
        if (!(level instanceof ServerLevel serverLevel)) return true;
        AABB activeArea = new AABB(pos).inflate(LOCAL_POPULATION_RADIUS, LOCAL_POPULATION_HEIGHT, LOCAL_POPULATION_RADIUS);
        return serverLevel.getEntities(type, activeArea, entity -> entity.isAlive()).isEmpty();
    }

    @Override
    protected BTRoot createBT() {
        BTNode defense = new BTNode() {
            @Override
            public BTStatus execute() {
                return BTStatus.RUNNING;
            }
        };
        BTNode active = SelectorNode.of(
                SequenceNode.of(
                        new HasTargetCondition(GraniteElemental.this),
                        new FlyingPursuitAction(GraniteElemental.this, 1.0)),
                new LookForwardWanderFlyAction(GraniteElemental.this, 0.18, 0.0F));
        BTNode root = new ConditionalSwitchNode(GraniteElemental.this::isInDefenseSequence, defense, active);

        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return root;
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_DEFENSE_PHASE, (byte) DefensePhase.ACTIVE.ordinal());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            advanceDefenseState();
        }
    }

    /// 推进防御状态，并在行为树之后再次固定防御速度。
    ///
    /// 速度约束放在实体逻辑末尾，可确保其他移动控制器不会在同一 tick 覆盖防御坠落。
    /// 进入与退出阶段仍保持悬浮；只有完整防御阶段启用重力并以固定速度向下落。
    void advanceDefenseState() {
        DefensePhase phase = getDefensePhase();
        if (phase == DefensePhase.ACTIVE) {
            setNoGravity(true);
            return;
        }

        if (phase == DefensePhase.DEFENDING) {
            setNoGravity(false);
            setDeltaMovement(new Vec3(0.0, -0.2, 0.0));
            hasImpulse = true;
        } else {
            setNoGravity(true);
            setDeltaMovement(Vec3.ZERO);
        }

        if (--defenseTicks > 0) {
            return;
        }
        switch (phase) {
            case ENTERING ->
                    setDefensePhase(DefensePhase.DEFENDING, stateParameters(DefensePhase.DEFENDING).duration());
            case DEFENDING ->
                    setDefensePhase(DefensePhase.EXITING, stateParameters(DefensePhase.EXITING).duration());
            case EXITING -> setDefensePhase(DefensePhase.ACTIVE, 0);
            case ACTIVE -> {
                // ACTIVE 已在方法开头返回，此分支仅用于保证枚举处理完整。
            }
        }
    }

    private void beginDefenseSequence() {
        if (getDefensePhase() == DefensePhase.ACTIVE) {
            setDefensePhase(DefensePhase.ENTERING, stateParameters(DefensePhase.ENTERING).duration());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && getDefensePhase() == DefensePhase.DEFENDING && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        boolean damaged = super.hurt(source, amount);
        if (damaged && isAlive() && getHealth() < getMaxHealth() * 0.5F
                && !level().isClientSide && getDefensePhase() == DefensePhase.ACTIVE
                && LibUtils.isAtLeastExpert(level(), blockPosition()) && random.nextInt(6) == 0) {
            beginDefenseSequence();
        }
        return damaged;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte(DEFENSE_PHASE_TAG, (byte) getDefensePhase().ordinal());
        tag.putInt(DEFENSE_TICKS_TAG, defenseTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        DefensePhase phase = DefensePhase.byId(tag.getByte(DEFENSE_PHASE_TAG));
        setDefensePhase(phase, Math.max(0, tag.getInt(DEFENSE_TICKS_TAG)));
    }

    @Override
    public boolean onGround() {
        return getDefensePhase() != DefensePhase.ACTIVE && super.onGround();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Defense", 0, state ->
                state.setAndContinue(switch (getDefensePhase()) {
                    case ACTIVE -> WALK;
                    case ENTERING -> TO_DEFENSE;
                    case DEFENDING -> DEFENSE;
                    case EXITING -> FROM_DEFENSE;
                })));
    }

    DefensePhase getDefensePhase() {
        return DefensePhase.byId(entityData.get(DATA_DEFENSE_PHASE));
    }

    int getDefenseTicks() {
        return defenseTicks;
    }

    boolean isInDefenseSequence() {
        return getDefensePhase() != DefensePhase.ACTIVE;
    }

    boolean hasTerrainCollision() {
        return !noPhysics;
    }

    /// 花岗精在防御阶段会落地，其他阶段仍按飞行怪物处理。
    @Override
    public boolean isNoGravity() {
        return getDefensePhase() != DefensePhase.DEFENDING;
    }

    private void setDefensePhase(DefensePhase phase, int ticks) {
        DefensePhase previous = getDefensePhase();
        if (previous != phase) setSpecialState(previous, false);
        entityData.set(DATA_DEFENSE_PHASE, (byte) phase.ordinal());
        setSpecialState(phase, true);
        defenseTicks = ticks;
        setNoGravity(phase != DefensePhase.DEFENDING);
    }

    public enum DefensePhase {
        ACTIVE,
        ENTERING,
        DEFENDING,
        EXITING;

        static DefensePhase byId(int id) {
            DefensePhase[] values = values();
            return values[Mth.clamp(id, 0, values.length - 1)];
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.GRANITE_GOLEM_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.GRANITE_GOLEM_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.GRANITE_GOLEM_DEATH.get();
    }

}
