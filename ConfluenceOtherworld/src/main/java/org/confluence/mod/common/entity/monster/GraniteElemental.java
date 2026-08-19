package org.confluence.mod.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyingPursuitAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 花岗岩元素。
///
/// <p>普通状态下会持续追逐目标；专家难度受到攻击时有概率进入防御循环。防御循环包含
/// 进入动画、防御坠落和退出动画三个阶段。阶段由服务端推进并通过实体数据同步，客户端
/// 只负责选择对应动画，避免多人环境中各客户端自行推算阶段而产生视觉分歧。</p>
///
/// <p>防御阶段会暂时恢复重力、停止横向移动并吸收普通伤害。花岗岩元素具有正常碰撞，不能像幽灵类生物一样
/// 穿墙，否则防御坠落会直接穿过地面。</p>
public class GraniteElemental extends BaseFlyingMonster {
    private static final EntityDataAccessor<Byte> DATA_DEFENSE_PHASE = SynchedEntityData.defineId(GraniteElemental.class, EntityDataSerializers.BYTE);

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation TO_DEFENSE = RawAnimation.begin().thenPlayAndHold("to_defense");
    private static final RawAnimation DEFENSE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation FROM_DEFENSE = RawAnimation.begin().thenPlayAndHold("from_defense");

    private static final int TRANSITION_TICKS = 7;
    private static final int DEFENDING_TICKS = 100;

    private int defenseTicks;

    public GraniteElemental(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 180, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.ARMOR, 6.0);
    }

    @Override
    protected BTRoot createBT() {
        BTNode defense = new BTNode() {
            @Override
            public BTStatus execute() {
                return BTStatus.RUNNING;
            }
        };
        BTNode active = SelectorNode.of(SequenceNode.of(new HasTargetCondition(GraniteElemental.this), new FlyingPursuitAction(GraniteElemental.this, 0.04, 0.4)), new FlyWanderAction(GraniteElemental.this, 0.2, 12));
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
    /// <p>速度约束放在实体逻辑末尾，可确保其他移动控制器不会在同一 tick 覆盖防御坠落。
    /// 进入与退出阶段仍保持悬浮；只有完整防御阶段启用重力并以固定速度向下落。</p>
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
            case ENTERING -> setDefensePhase(DefensePhase.DEFENDING, DEFENDING_TICKS);
            case DEFENDING -> setDefensePhase(DefensePhase.EXITING, TRANSITION_TICKS);
            case EXITING -> setDefensePhase(DefensePhase.ACTIVE, 0);
            case ACTIVE -> {
                // ACTIVE 已在方法开头返回，此分支仅用于保证枚举处理完整。
            }
        }
    }

    private void beginDefenseSequence() {
        if (getDefensePhase() == DefensePhase.ACTIVE) {
            setDefensePhase(DefensePhase.ENTERING, TRANSITION_TICKS);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && getDefensePhase() == DefensePhase.DEFENDING) {
            return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
        }

        if (!level().isClientSide && getDefensePhase() == DefensePhase.ACTIVE && LibUtils.isAtLeastExpert(level(), blockPosition()) && random.nextFloat() < 0.2F) {
            beginDefenseSequence();
        }
        return super.hurt(source, amount);
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
        entityData.set(DATA_DEFENSE_PHASE, (byte) phase.ordinal());
        defenseTicks = ticks;
        setNoGravity(phase != DefensePhase.DEFENDING);
    }

    enum DefensePhase {
        ACTIVE,
        ENTERING,
        DEFENDING,
        EXITING;

        static DefensePhase byId(int id) {
            DefensePhase[] values = values();
            return values[Mth.clamp(id, 0, values.length - 1)];
        }
    }
}
