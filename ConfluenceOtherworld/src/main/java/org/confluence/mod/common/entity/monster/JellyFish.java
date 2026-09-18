package org.confluence.mod.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
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
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomSwimAction;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

/// 三种水母共用的脉冲游动与两阶段战斗实现。
///
/// 水母并不是持续贴身攻击的普通鱼类。发现水中的玩家后，它会先追逐 150 tick，
/// 再停止寻路并进入 80 tick 的定向脉冲阶段，随后主动释放目标并重新游荡。阶段字段由
/// 服务端同步，客户端只据此选择动画，不自行推算战斗时序。
public class JellyFish extends BaseAquaticMonster {
    private static final int PULSE_TICKS = 80;
    private static final int ATTACK_PULSE_INTERVAL = 20;
    private static final double PULSE_SPEED_FACTOR = 0.1;
    private static final double SHORE_TARGET_RANGE = 6.0;
    private static final double SHORE_TARGET_HEIGHT = 3.0;
    private static final EntityDataAccessor<Boolean> ATTACK_PHASE = SynchedEntityData.defineId(JellyFish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ELECTRIFIED = SynchedEntityData.defineId(JellyFish.class, EntityDataSerializers.BOOLEAN);

    private final Profile profile;

    /// 渲染器使用相邻两次有效速度插值模型朝向，避免每次脉冲时突然翻转。
    public Vec3 lastMovement = Vec3.ZERO;
    public Vec3 currentMovement = Vec3.ZERO;

    public JellyFish(EntityType<? extends JellyFish> type, Level level) {
        this(type, level, Profile.ROUTINE);
    }

    public JellyFish(EntityType<? extends JellyFish> type, Level level, Profile profile) {
        super(type, level);
        this.profile = profile;
        this.moveControl = new JellyFishMoveControl(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ATTACK_PHASE, false);
        entityData.define(ELECTRIFIED, false);
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

    /// 水中的玩家始终是有效目标；靠近水岸且可见的玩家也会触发水母向水面脉冲，
    /// 使其能够跃出水面攻击，但不会在搁浅后继续把自己当作陆地怪物追踪。
    @Override
    protected boolean isValidAquaticTarget(LivingEntity target) {
        if (target.isInWaterOrBubble()) return true;
        if (!isInWaterOrBubble() || !hasLineOfSight(target)) return false;
        double x = target.getX() - getX();
        double z = target.getZ() - getZ();
        double y = target.getY() - getY();
        return x * x + z * z <= SHORE_TARGET_RANGE * SHORE_TARGET_RANGE
                && y >= -1.0 && y <= SHORE_TARGET_HEIGHT;
    }

    /// 返回服务端同步的脉冲阶段，供动画与发光层选择表现。
    public boolean isAttackPhase() {
        return entityData.get(ATTACK_PHASE);
    }

    /// 带电是专家模式独有的防御状态，不能与普通的收缩推进动画混为一谈。
    public boolean isElectrified() {
        return entityData.get(ELECTRIFIED);
    }

    private void setAttackPhase(boolean attackPhase) {
        entityData.set(ATTACK_PHASE, attackPhase);
        entityData.set(ELECTRIFIED, profile != Profile.FUNGO && attackPhase && isInWater() && LibUtils.isAtLeastExpert(level(), blockPosition()));
        setSpecialState(CombatState.ELECTRIFIED, isElectrified());
    }

    /// 水母使用独立接触伤害冷却，不把伤害绑在攻击动画帧上。
    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    @Override
    protected boolean flopsOnLand() {
        return false;
    }

    @Override
    public void tick() {
        if (!level().isClientSide && isElectrified() && (!isInWater() || horizontalCollision || verticalCollision)) {
            entityData.set(ELECTRIFIED, false);
            setSpecialState(CombatState.ELECTRIFIED, false);
        }
        if (level().isClientSide && getDeltaMovement().length() > 0.08) {
            lastMovement = getDeltaMovement();
        }
        super.tick();
        if (getDeltaMovement().length() > 0.08) {
            currentMovement = getDeltaMovement();
        }
    }

    /// 带电阶段保持上一次脉冲速度，直到碰到实体方块或离开水面。
    @Override
    public void travel(Vec3 travelVector) {
        if (isEffectiveAi() && isElectrified() && isInWater()) {
            move(MoverType.SELF, getDeltaMovement());
            return;
        }
        super.travel(travelVector);
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

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean damaged = super.doHurtTarget(target);
        if (damaged && target instanceof LivingEntity living && profile.silences && random.nextInt(5) == 0) {
            int duration = LibUtils.isMaster(level(), blockPosition()) ? 350
                    : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 280 : 140;
            living.addEffect(new MobEffectInstance(ModEffects.SILENCED.get(), duration), this);
        }
        return damaged;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isElectrified() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (!level().isClientSide && source.getDirectEntity() == source.getEntity()
                    && source.getEntity() instanceof LivingEntity attacker) {
                attacker.hurt(damageSources().thorns(this), (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.3));
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    public enum CombatState {PURSUING, PULSING, ELECTRIFIED}

    /// 执行水母的追逐—脉冲周期，并在攻击阶段进行定向推进。
    private static final class JellyFishCombatAction extends BTNode {
        private final JellyFish jellyfish;
        private int phaseTicks;
        private int repathTicks;

        private JellyFishCombatAction(JellyFish jellyfish) {
            this.jellyfish = jellyfish;
        }

        @Override
        public void start() {
            phaseTicks = 0;
            repathTicks = 0;
            jellyfish.setAttackPhase(false);
        }

        @Override
        public boolean canStart() {
            var target = jellyfish.getTarget();
            return target != null && target.isAlive() && jellyfish.canAttack(target);
        }

        @Override
        public BTStatus execute() {
            var target = jellyfish.getTarget();
            if (target == null || !jellyfish.canAttack(target)) {
                jellyfish.setAttackPhase(false);
                if (phaseTicks > 0) jellyfish.getNavigation().stop();
                return BTStatus.FAILURE;
            }

            int pursuitTicks = jellyfish.stateParameters(CombatState.PURSUING).duration();
            var pulse = jellyfish.stateParameters(CombatState.PULSING);
            if (phaseTicks < pursuitTicks) {
                jellyfish.setAttackPhase(false);
                if (--repathTicks <= 0) {
                    jellyfish.getNavigation().moveTo(target, 1.0);
                    repathTicks = 10;
                }
                phaseTicks++;
                return BTStatus.RUNNING;
            }

            if (phaseTicks < pursuitTicks + pulse.durationOr(PULSE_TICKS)) {
                if (!jellyfish.isAttackPhase()) {
                    jellyfish.getNavigation().stop();
                    jellyfish.setAttackPhase(true);
                }
                if ((phaseTicks - pursuitTicks) % pulse.attackIntervalOr(ATTACK_PULSE_INTERVAL) == 0) {
                    pulseToward(target);
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
            if (phaseTicks > 0) jellyfish.getNavigation().stop();
            phaseTicks = 0;
            repathTicks = 0;
            jellyfish.setAttackPhase(false);
        }

        /// 每次收缩都朝目标中心推进，避免攻击阶段只发光却停在原地。
        private void pulseToward(net.minecraft.world.entity.LivingEntity target) {
            Vec3 direction = jellyfish.position().vectorTo(
                    target.position().add(0.0, target.getBbHeight() * 0.5, 0.0));
            if (direction.lengthSqr() <= 1.0E-6) {
                return;
            }
            jellyfish.faceCombatDirection(direction, 30.0F, 30.0F);
            jellyfish.setDeltaMovement(direction.normalize().scale(jellyfish.getAttributeValue(Attributes.MOVEMENT_SPEED) * PULSE_SPEED_FACTOR));
            jellyfish.hasImpulse = true;
        }
    }

    /// 水母以离散脉冲修正方向，而不是像普通鱼一样连续推进。
    ///
    /// 每次导航请求到达冷却边沿时才消费目标位置并重设速度；无目标时冷却范围更大，
    /// 战斗时则更频繁。攻击阶段的推进完全由战斗动作负责，不再消费导航航点。
    private static final class JellyFishMoveControl extends MoveControl {
        private static final int BASE_PULSE_COOLDOWN = 20;
        private int pulseCooldown;

        private JellyFishMoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            if (((JellyFish) mob).isAttackPhase() || mob.getNavigation().isDone()) {
                operation = Operation.WAIT;
                mob.setSpeed(0.0F);
                mob.setXxa(0.0F);
                mob.setYya(0.0F);
                mob.setZza(0.0F);
                return;
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
                mob.setYBodyRot(mob.getYRot());
                mob.setYHeadRot(mob.getYRot());
                // 速度由脉冲直接设置，不再叠加 travel 的输入加速度。
                mob.setSpeed(0.0F);
                mob.setXxa(0.0F);
                mob.setYya(0.0F);
                mob.setZza(0.0F);
                mob.setDeltaMovement(new Vec3(xDistance, yDistance, zDistance).normalize()
                        .scale(speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * PULSE_SPEED_FACTOR));

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
                        + mob.getRandom1211().nextInt(randomRange);
                return;
            }

        }
    }

    /// 共享水母状态机中的接触效果档案；颜色、属性和生成条件仍由注册与数据层负责。
    public enum Profile {
        ROUTINE(false),
        GREEN(true),
        FUNGO(false);

        private final boolean silences;

        Profile(boolean silences) {
            this.silences = silences;
        }
    }
}
