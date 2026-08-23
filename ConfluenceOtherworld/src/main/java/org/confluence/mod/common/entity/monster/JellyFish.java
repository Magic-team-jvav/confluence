package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomSwimAction;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

/// 三种水母共用的脉冲游动与两阶段战斗实现。
///
/// 水母并不是持续贴身攻击的普通鱼类。发现水中的玩家后，它会先追逐 150 tick，
/// 再停止寻路并进入 80 tick 的快速脉冲阶段，随后主动释放目标并重新游荡。阶段字段由
/// 服务端同步，客户端只据此选择动画，不自行推算战斗时序。
public class JellyFish extends BaseAquaticMonster {
    private static final int PURSUIT_TICKS = 150;
    private static final int PULSE_TICKS = 80;
    private static final EntityDataAccessor<Boolean> ATTACK_PHASE = SynchedEntityData.defineId(JellyFish.class, EntityDataSerializers.BOOLEAN);

    /// 渲染器使用相邻两次有效速度插值模型朝向，避免每次脉冲时突然翻转。
    public Vec3 lastMovement = Vec3.ZERO;
    public Vec3 currentMovement = Vec3.ZERO;

    public JellyFish(EntityType<? extends JellyFish> type, Level level) {
        super(type, level);
        this.moveControl = new JellyFishMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AquaticAttributeProfiles.BLUE_JELLYFISH.createBuilder();
    }

    public static AttributeSupplier.Builder createPinkAttributes() {
        return AquaticAttributeProfiles.PINK_JELLYFISH.createBuilder();
    }

    public static AttributeSupplier.Builder createGreenAttributes() {
        return AquaticAttributeProfiles.GREEN_JELLYFISH.createBuilder();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACK_PHASE, false);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new TryFindWaterGoal(JellyFish.this)),
                        createCombatAction(),
                        new RandomSwimAction(JellyFish.this, 1.0, 10, 3),
                        new VanillaGoalAction(new RandomLookAroundGoal(JellyFish.this)),
                        new VanillaGoalAction(new LookAtPlayerGoal(JellyFish.this, Player.class, 6.0F)));
            }
        };
    }

    private BTNode createCombatAction() {
        return new JellyFishCombatAction(this);
    }

    /// 返回服务端同步的脉冲阶段，供动画与发光层选择表现。
    public boolean isAttackPhase() {
        return entityData.get(ATTACK_PHASE);
    }

    private void setAttackPhase(boolean attackPhase) {
        entityData.set(ATTACK_PHASE, attackPhase);
    }

    @Override
    public void tick() {
        if (level().isClientSide && getDeltaMovement().length() > 0.08) {
            lastMovement = getDeltaMovement();
        }
        super.tick();
        if (getDeltaMovement().length() > 0.08) {
            currentMovement = getDeltaMovement();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Swim/Pulse", 2, state -> state.setAndContinue(isAttackPhase()
                ? DefaultAnimations.ATTACK_STRIKE : DefaultAnimations.SWIM)));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.JELLYFISH_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.JELLYFISH_FREE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.JELLYFISH_DEATH.get();
    }

    /// 复现 1.21 的追逐—脉冲循环，同时把状态切换保留在新的行为树架构内。
    private static final class JellyFishCombatAction extends BTNode {
        private final JellyFish jellyfish;
        private int phaseTicks;

        private JellyFishCombatAction(JellyFish jellyfish) {
            this.jellyfish = jellyfish;
        }

        @Override
        public void start() {
            phaseTicks = 0;
            jellyfish.setAttackPhase(false);
        }

        @Override
        public BTStatus execute() {
            var target = jellyfish.getTarget();
            if (target == null || !target.isInWater() || !jellyfish.canAttack(target)) {
                jellyfish.setAttackPhase(false);
                jellyfish.getNavigation().stop();
                return BTStatus.FAILURE;
            }

            if (phaseTicks < PURSUIT_TICKS) {
                jellyfish.setAttackPhase(false);
                jellyfish.getNavigation().moveTo(target, 1.0);
                phaseTicks++;
                return BTStatus.RUNNING;
            }

            if (phaseTicks < PURSUIT_TICKS + PULSE_TICKS) {
                if (!jellyfish.isAttackPhase()) {
                    jellyfish.getNavigation().stop();
                    jellyfish.setAttackPhase(true);
                }
                phaseTicks++;
                return BTStatus.RUNNING;
            }

            jellyfish.setAttackPhase(false);
            jellyfish.setTarget(null);
            return BTStatus.SUCCESS;
        }

        @Override
        public void stop() {
            phaseTicks = 0;
            jellyfish.setAttackPhase(false);
        }
    }

    /// 水母以离散脉冲修正方向，而不是像普通鱼一样连续推进。
    ///
    /// 每次导航请求到达冷却边沿时才消费目标位置并重设速度；无目标时冷却范围更大，
    /// 战斗时则更频繁。等待期间仍面向当前目标，使脉冲间隔不会表现为完全静止。
    private static final class JellyFishMoveControl extends MoveControl {
        private static final int BASE_PULSE_COOLDOWN = 20;
        private int pulseCooldown;

        private JellyFishMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (mob.isInWater()) {
                mob.setDeltaMovement(mob.getDeltaMovement().add(0.0, 0.005, 0.0));
            }

            if (--pulseCooldown <= 0 && operation == Operation.MOVE_TO) {
                operation = Operation.WAIT;
                double xDistance = wantedX - mob.getX();
                double zDistance = wantedZ - mob.getZ();
                double yDistance = wantedY - mob.getY();
                double distanceSqr = xDistance * xDistance
                        + yDistance * yDistance
                        + zDistance * zDistance;
                if (distanceSqr < 2.500000277905201E-7) {
                    mob.setZza(0.0F);
                    return;
                }

                float targetYaw = (float) (Mth.atan2(zDistance, xDistance) * Mth.RAD_TO_DEG) - 90.0F;
                mob.setYRot(rotlerp(mob.getYRot(), targetYaw, 90.0F));
                mob.setSpeed((float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                mob.setDeltaMovement(mob.getDeltaMovement().normalize().scale(0.5));

                BlockPos blockPos = mob.blockPosition();
                BlockState blockState = mob.level().getBlockState(blockPos);
                VoxelShape collisionShape = blockState.getCollisionShape(mob.level(), blockPos);
                boolean targetAboveStep = yDistance > mob.maxUpStep() && xDistance * xDistance + zDistance * zDistance < Math.max(1.0F, mob.getBbWidth());
                boolean blockedAbove = !collisionShape.isEmpty() && mob.getY() < collisionShape.max(Direction.Axis.Y) + blockPos.getY() && !blockState.is(BlockTags.DOORS) && !blockState.is(BlockTags.FENCES);
                if (targetAboveStep || blockedAbove) {
                    mob.getJumpControl().jump();
                }

                int randomRange = mob.getTarget() == null
                        ? BASE_PULSE_COOLDOWN * 3
                        : BASE_PULSE_COOLDOWN;
                pulseCooldown = BASE_PULSE_COOLDOWN
                        + mob.getRandom().nextInt(randomRange);
                return;
            }

            if (mob.getTarget() != null) {
                mob.getLookControl().setLookAt(mob.getTarget(), 10.0F, 10.0F);
            }
        }
    }
}
