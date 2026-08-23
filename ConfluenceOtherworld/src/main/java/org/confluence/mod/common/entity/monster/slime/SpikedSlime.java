package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

/// 尖刺史莱姆 —— 有目标时发射 8 方向尖刺并跳跃追击。
public class SpikedSlime extends BaseSlime {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation JUMP = RawAnimation.begin().thenPlay("jump");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
    private static final double CLOSE_ATTACK_DISTANCE = 7.0;

    public SpikedSlime(EntityType<? extends BaseSlime> type, Level level) {
        this(type, level, 0x4B6E8C, false);
    }

    protected SpikedSlime(EntityType<? extends BaseSlime> type, Level level, int color, boolean passiveByDay) {
        super(type, level, color, passiveByDay);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(7.0f, 5, 26.0f);
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

    /// 丛林尖刺史莱姆可在远距离跳跃前偶尔补射一发；普通与冰雪变体保持纯追击。
    protected boolean canFireDistantSingleSpike() {
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
        for (int i = 0; i < spikeCount(); i++) {
            double angle = baseAngle + Math.PI * 2.0 * i / spikeCount();
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

    /// 复刻 1.21 的尖刺史莱姆战斗时序。
    ///
    /// 近距离先瞄准二十刻，再蓄力五刻并以三刻间隔发射三轮八向弹幕；远距离
    /// 瞄准后蓄力跳向目标。目标在动作中途失效时立即失败，由选择节点重新决策。
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
            closeRange = target != null && distanceToSqr(target) < CLOSE_ATTACK_DISTANCE * CLOSE_ATTACK_DISTANCE;
            if (!closeRange) {
                distantShotBranch = canFireDistantSingleSpike() && random.nextInt(3) == 0;
                int extraWait = canFireDistantSingleSpike() && !distantShotBranch ? 4 : 0;
                distantTriggerTick = 20 + extraWait;
                distantJumpTick = 29 + extraWait;
                distantFinishTick = 38 + extraWait;
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
                getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            if (closeRange) {
                if (tick == 20) {
                    triggerAnim("Controller", "attack");
                }
                if (tick == 24 || tick == 26 || tick == 28) {
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
