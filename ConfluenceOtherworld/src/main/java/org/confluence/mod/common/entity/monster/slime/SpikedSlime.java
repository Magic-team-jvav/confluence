package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.SlimeHopAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 尖刺史莱姆的跳跃与射击公共状态机。
public class SpikedSlime extends BaseSlime {
    public static final float MODEL_SCALE = 1.5F;
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlay("jump");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final double CLOSE_ATTACK_DISTANCE = 7.0;

    public SpikedSlime(EntityType<? extends BaseSlime> type, Level level) {
        this(type, level, false);
    }

    protected SpikedSlime(EntityType<? extends BaseSlime> type, Level level, boolean passiveByDay) {
        super(type, level, passiveByDay);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions().scale(MODEL_SCALE * getScale());
    }

    protected int spikeCount() {
        return 8;
    }

    protected float spikeDamage() {
        return (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    protected SlimeSpikeEntity.Variant spikeVariant() {
        return SlimeSpikeEntity.Variant.NORMAL;
    }

    /// 是否使用生态尖刺史莱姆的难度分层射击方式。
    protected boolean usesBiomeSpikePattern() {
        return false;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(SequenceNode.of(new HasTargetCondition(SpikedSlime.this), createCombatAction()), SequenceNode.of(new WaitAction(20 + random.nextInt(40)), new SlimeHopAction(SpikedSlime.this, false)));
            }
        };
    }

    private BTNode createCombatAction() {
        return new CombatAction();
    }

    private void fireRadialVolley() {
        double baseAngle = random.nextDouble() * Math.PI * 2.0;
        double verticalAngle = random.nextDouble() * 0.3 + 0.05;
        double horizontalScale = Math.cos(verticalAngle);
        int count = usesBiomeSpikePattern() ? 6 + random.nextInt(3) : spikeCount();
        for (int i = 0; i < count; i++) {
            double angle = baseAngle + Math.PI * 2.0 * i / count;
            SlimeSpikeEntity spike = SlimeSpikeEntity.create(level(), this, ModEntities.SLIME_SPIKE.get(), Math.cos(angle) * horizontalScale, Math.sin(verticalAngle), Math.sin(angle) * horizontalScale, 0.3F, 1.0F, spikeDamage(), spikeVariant(), true);
            spike.setPos(getBoundingBox().getCenter().offsetRandom(random, 0.2F));
            level().addFreshEntity(spike);
        }
    }

    private void fireDistantSpike(LivingEntity target) {
        Vec3 direction = target.getEyePosition().subtract(getEyePosition());
        SlimeSpikeEntity spike = SlimeSpikeEntity.create(level(), this, ModEntities.SLIME_SPIKE.get(), direction.x, direction.y, direction.z, 0.3F, 1.0F, spikeDamage(), spikeVariant(), false);
        spike.setPos(getBoundingBox().getCenter().offsetRandom(random, 0.2F));
        level().addFreshEntity(spike);
    }

    private void jumpToward(LivingEntity target) {
        Vec3 offset = target.position().subtract(position());
        double horizontalDistance = Math.sqrt(offset.x * offset.x + offset.z * offset.z);
        Vec3 horizontal = horizontalDistance > 1.0E-4 ? new Vec3(offset.x / horizontalDistance, 0.0, offset.z / horizontalDistance) : Vec3.ZERO;
        double vertical = target.getY() + 4.0 < getY() ? 0.92 : 0.42;
        setDeltaMovement(horizontal.x, vertical, horizontal.z);
        hasImpulse = true;
    }

    /// 执行尖刺史莱姆的战斗时序。
    ///
    /// 普通尖刺史莱姆近距离发射三轮八向弹幕；丛林和冰雪变体在经典模式瞄准单发，
    /// 专家及以上难度近距离改为一次六至八向环射。目标失效时立即重新决策。
    private final class CombatAction extends BTNode {
        private int tick;
        private boolean closeRange;
        private boolean distantShotBranch;
        private int distantTriggerTick;
        private int distantJumpTick;
        private int distantFinishTick;

        @Override
        public void start() {
            tick = 0;
            LivingEntity target = getTarget();
            boolean expert = LibUtils.isAtLeastExpert(level(), blockPosition());
            closeRange = target != null && distanceToSqr(target) < CLOSE_ATTACK_DISTANCE * CLOSE_ATTACK_DISTANCE
                    && (!usesBiomeSpikePattern() || expert);
            if (!closeRange) {
                distantShotBranch = usesBiomeSpikePattern();
                distantTriggerTick = 20;
                distantJumpTick = 29;
                distantFinishTick = 38;
            }
        }

        @Override
        public BTStatus execute() {
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive()) {
                return BTStatus.FAILURE;
            }
            tick++;

            if (tick <= 20) {
                faceCombatPosition(target.getEyePosition(), 30.0F, 30.0F);
            }

            if (closeRange) {
                if (tick == 20) {
                    triggerAnim("Controller", "attack");
                }
                if ((usesBiomeSpikePattern() && tick == 24)
                        || (!usesBiomeSpikePattern() && (tick == 24 || tick == 26 || tick == 28))) {
                    fireRadialVolley();
                }
                return tick >= 49 ? BTStatus.SUCCESS : BTStatus.RUNNING;
            }

            if (tick == 20 && distantShotBranch) {
                fireDistantSpike(target);
            }
            if (tick == distantTriggerTick) {
                if (distantShotBranch && tick != 20) {
                    fireDistantSpike(target);
                }
                triggerAnim("Controller", "jump");
            }
            if (tick == distantJumpTick) {
                faceCombatPosition(target.getEyePosition(), 180.0F, 180.0F);
                jumpToward(target);
            }
            return tick >= distantFinishTick ? BTStatus.SUCCESS : BTStatus.RUNNING;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Controller", 5, state -> state.setAndContinue(IDLE)).triggerableAnim("jump", JUMP).triggerableAnim("attack", ATTACK));
    }
}
