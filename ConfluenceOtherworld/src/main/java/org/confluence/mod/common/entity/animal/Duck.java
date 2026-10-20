package org.confluence.mod.common.entity.animal;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.IntFunction;

/// 鸭子独立管理陆地巡游、水面游泳、受惊起飞和降落。
public class Duck extends BaseCritter implements VariantHolder<Duck.Variant> {
    public static final String VARIANT_KEY = "Variant";
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("move.fly");
    private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(Duck.class, EntityDataSerializers.BOOLEAN);
    private static final double SWIM_SPEED = 0.08;
    private static final double FLIGHT_SPEED = 0.20;
    private static final int FLIGHT_DURATION = 60;
    private static final int FLIGHT_COOLDOWN = 100;
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Duck.class, EntityDataSerializers.INT);
    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.MALLARD, 1)
            .add(Variant.COMMON, 1)
            .build();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final MoveControl groundControl;
    private final MoveControl flightControl;
    private DuckBehavior behavior;

    public Duck(EntityType<? extends Duck> type, Level level) {
        super(type, level);
        groundControl = moveControl;
        flightControl = new MoveControl(this);
        getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY.value()).setBaseValue(1.0);
        setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                behavior = new DuckBehavior();
                return behavior;
            }
        };
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation navigation = new GroundPathNavigation(this, level);
        navigation.setCanFloat(true);
        return navigation;
    }

    private void setMovementMode(MovementMode mode) {
        MoveControl nextControl = switch (mode) {
            case LAND -> groundControl;
            case WATER, FLYING, LANDING -> flightControl;
        };
        if (moveControl != nextControl) {
            navigation.stop();
            moveControl = nextControl;
            moveControl.setWantedPosition(getX(), getY(), getZ(), 0.0);
            setSpeed(0.0F);
            setXxa(0.0F);
            setYya(0.0F);
            setZza(0.0F);
        }
        setNoGravity(mode != MovementMode.LAND);
        entityData.set(DATA_FLYING, mode == MovementMode.FLYING || mode == MovementMode.LANDING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_VARIANT, Variant.MALLARD.ordinal());
        entityData.define(DATA_FLYING, false);
    }

    @Override
    public Variant getVariant() {
        return Variant.BY_ID.apply(entityData.get(DATA_VARIANT));
    }

    @Override
    public void setVariant(Variant variant) {
        entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(SPAWN_VARIANTS.select(random));
    }

    @Override
    protected String variantSaveKey() {
        return VARIANT_KEY;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getVariant().serialize(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.MALLARD);
        } else {
            PortDataResultExtension.ifSuccess(Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)), this::setVariant);
        }
        if (behavior != null) behavior.reset();
        else setMovementMode(MovementMode.LAND);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (onGround || isInWaterOrBubble()) fallDistance = 0.0F;
        else if (y < 0.0) fallDistance -= (float) y;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    /// 飞行状态只用于同步和动画；陆地保留普通动物物理，避免 FlyingAnimal 的竖直阻尼压低跳跃。
    public boolean isFlying() {
        return entityData.get(DATA_FLYING);
    }

    @Override
    public void tick() {
        /// NoAI 时行为树不运行，仍需解除可能残留的飞行控制。
        if (!level().isClientSide && isNoAi() && behavior != null) behavior.reset();
        super.tick();
        if (!level().isClientSide && !isNoAi()) {
            /// 朝向跟随实际位移，不能用随机注视方向覆盖寻路方向。
            Vec3 movement = new Vec3(getX() - xo, 0.0, getZ() - zo);
            if (movement.horizontalDistanceSqr() > 1.0E-5) {
                float yaw = (float) (Mth.atan2(movement.z, movement.x) * Mth.RAD_TO_DEG) - 90.0F;
                setYRot(yaw);
                setYBodyRot(yaw);
                setYHeadRot(yaw);
            }
        }
        if (level().isClientSide && isInWater() && tickCount % 16 == 0) {
            level().addParticle(ParticleTypes.BUBBLE_POP, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public ResourceLocation getModelPath() {
        return getVariant().modelPath();
    }

    @Override
    public ResourceLocation getTexturePath() {
        return getVariant().texturePath();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 3, state -> {
            state.getController().setAnimationSpeed(1.0);
            if (isFlying()) return state.setAndContinue(FLY);
            if (isInWater()) return state.setAndContinue(SWIM);
            double dx = getX() - xo;
            double dz = getZ() - zo;
            boolean walking = onGround() && dx * dx + dz * dz > 1.0E-5;
            if (walking)
                state.getController().setAnimationSpeed(Mth.clamp(Math.sqrt(dx * dx + dz * dz) / 0.06, 0.5, 2.0));
            return state.setAndContinue(walking ? WALK : IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted && isAlive() && !level().isClientSide && behavior != null) {
            behavior.requestFlight(source.getEntity());
        }
        return accepted;
    }

    /// 水面和飞行使用各自的速度，不再叠加地面 MoveControl 或原版水中加速。
    @Override
    public void travel(Vec3 input) {
        if (isEffectiveAi() && !isNoAi() && behavior != null && behavior.mode != MovementMode.LAND) {
            Vec3 motion = getDeltaMovement();
            if (behavior.mode == MovementMode.WATER) {
                double surface = waterSurface();
                double vertical = Double.isNaN(surface) ? -0.04
                        : Mth.clamp((surface - getBbHeight() * 0.15 - getY()) * 0.3, -0.10, 0.10);
                motion = new Vec3(motion.x, vertical, motion.z);
            }
            move(MoverType.SELF, motion);
            setDeltaMovement(getDeltaMovement().multiply(0.90, 1.0, 0.90));
            calculateEntityAnimation(false);
            return;
        }
        super.travel(input);
    }

    /// 只读取当前水柱的实际液面；离水后不能把空气方块顶面当成液面。
    private double waterSurface() {
        BlockPos pos = blockPosition();
        if (!level().getFluidState(pos).is(FluidTags.WATER)) pos = pos.below();
        if (!level().getFluidState(pos).is(FluidTags.WATER)) return Double.NaN;
        while (pos.getY() < level().getMaxBuildHeight() - 1 && level().getFluidState(pos.above()).is(FluidTags.WATER))
            pos = pos.above();
        return pos.getY() + level().getFluidState(pos).getHeight(level(), pos);
    }

    private enum MovementMode {
        LAND, WATER, FLYING, LANDING
    }

    /// 管理巡游、游泳、受惊起飞和降落，并在状态切换时结束上一动作。
    private final class DuckBehavior extends BTNode {
        private final BTNode landRoutine = routine(new WaterAvoidingRandomStrollGoal(Duck.this, 1.0D));
        private MovementMode mode = MovementMode.LAND;
        private BTNode active;
        private Vec3 flightDirection = Vec3.ZERO;
        private int flightTicks;
        private int flightCooldown;
        private int swimTurnTicks;
        private int shoreTicks;
        private int swimPauseTicks;
        private Vec3 swimDirection = Vec3.ZERO;
        private boolean flewThisFall;
        private boolean flightRequested;
        private Entity threat;

        private BTNode routine(Goal movement) {
            return SelectorNode.of(
                    new VanillaGoalAction(movement),
                    new VanillaGoalAction(new LookAtPlayerGoal(Duck.this, Player.class, 6.0F)),
                    new VanillaGoalAction(new RandomLookAroundGoal(Duck.this))
            );
        }

        private void requestFlight(Entity attacker) {
            if (!airborne() && flightCooldown == 0) {
                flightRequested = true;
                threat = attacker;
            }
        }

        private boolean airborne() {
            return mode == MovementMode.FLYING || mode == MovementMode.LANDING;
        }

        private void changeMode(MovementMode next) {
            if (mode == next) return;
            stopAction();
            mode = next;
            setMovementMode(next);
            if (next == MovementMode.WATER) {
                swimDirection = Vec3.directionFromRotation(0, getYRot());
                swimTurnTicks = 100 + random.nextInt(100);
            }
        }

        private void updateEnvironment() {
            boolean inWater = isInWater();
            if (shoreTicks > 0 && inWater) return;
            if (onGround() || inWater) flewThisFall = false;
            if (mode == MovementMode.FLYING && (onGround() || inWater) && flightTicks < FLIGHT_DURATION - 10) {
                changeMode(inWater ? MovementMode.WATER : MovementMode.LAND);
            }
            if (mode == MovementMode.FLYING) return;
            if (mode == MovementMode.LANDING && !onGround() && !inWater) return;
            MovementMode next = inWater ? MovementMode.WATER : MovementMode.LAND;
            if (next != mode) {
                flightTicks = 0;
                flightDirection = Vec3.ZERO;
                changeMode(next);
            }
        }

        @Override
        public BTStatus execute() {
            if (isNoAi() || !isAlive()) {
                reset();
                return BTStatus.SUCCESS;
            }
            if (flightCooldown > 0) flightCooldown--;
            if (shoreTicks > 0) {
                shoreTicks--;
                if (!isInWater() && onGround()) shoreTicks = 0;
                else if (shoreTicks > 0 && !navigation.isDone()) {
                    if (horizontalCollision || isInWater()) getJumpControl().jump();
                    return BTStatus.RUNNING;
                } else shoreTicks = 0;
            }
            updateEnvironment();
            if (mode == MovementMode.WATER && !flightRequested) {
                Player player = level().getNearestPlayer(Duck.this, 3.0);
                if (player != null && !player.isSpectator() && !player.isCreative() && hasLineOfSight(player))
                    requestFlight(player);
            }
            if (flightRequested || mode == MovementMode.LAND && !onGround()
                    && !flewThisFall && fallDistance >= 3.0F) beginFlight();
            if (airborne()) {
                tickFlight();
                return BTStatus.RUNNING;
            }
            if (mode == MovementMode.WATER) {
                tickSwimming();
                return BTStatus.RUNNING;
            }
            if (active == null) {
                active = landRoutine;
                active.start();
            }
            if (active.execute() != BTStatus.RUNNING) stopAction();
            return BTStatus.RUNNING;
        }

        private void beginFlight() {
            Vec3 direction = threat == null ? getLookAngle() : position().subtract(threat.position());
            direction = direction.multiply(1.0, 0.0, 1.0);
            if (direction.lengthSqr() < 1.0E-6) {
                double angle = random.nextDouble() * Math.PI * 2.0;
                direction = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
            }
            flightDirection = direction.normalize();
            flightRequested = false;
            threat = null;
            flewThisFall = true;
            flightTicks = FLIGHT_DURATION;
            flightCooldown = FLIGHT_COOLDOWN;
            changeMode(MovementMode.FLYING);
            setDeltaMovement(flightDirection.scale(FLIGHT_SPEED).add(0.0, 0.25, 0.0));
            hasImpulse = true;
        }

        /// 优先沿当前方向游动，遇到可站立的岸边时交回地面寻路和跳跃。
        private void tickSwimming() {
            if (swimPauseTicks > 0) {
                swimPauseTicks--;
                setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
                return;
            }
            if (--swimTurnTicks <= 0) {
                Vec3 candidate = swimDirection.yRot((random.nextFloat() - 0.5F) * Mth.HALF_PI);
                if (canSwim(candidate)) swimDirection = candidate;
                swimTurnTicks = 100 + random.nextInt(100);
            }
            if (!canSwim(swimDirection)) {
                if (tryLeaveWater()) return;
                Vec3 next = Vec3.ZERO;
                for (int turn = 1; turn <= 8; turn++) {
                    Vec3 candidate = swimDirection.yRot(turn * Mth.PI / 4);
                    if (canSwim(candidate)) {
                        next = candidate;
                        break;
                    }
                }
                if (next.lengthSqr() > 0.0) swimDirection = next;
                /// 狭窄水域无路时停留，不每 tick 转动身体试探岸边。
                swimPauseTicks = 30;
                setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
                return;
            }
            Vec3 desired = swimDirection.scale(SWIM_SPEED);
            Vec3 current = getDeltaMovement();
            setDeltaMovement(Mth.lerp(0.2, current.x, desired.x), current.y, Mth.lerp(0.2, current.z, desired.z));
        }

        /// 检查前方完整碰撞箱和脚下水体，避免只看一个水方块就认为能够通过。
        private boolean canSwim(Vec3 direction) {
            Vec3 offset = direction.scale(0.45);
            if (!level().noCollision(Duck.this, getBoundingBox().move(offset))) return false;
            double half = getBbWidth() * 0.5;
            for (int x = -1; x <= 1; x += 2) {
                for (int z = -1; z <= 1; z += 2) {
                    BlockPos pos = BlockPos.containing(getX() + offset.x + x * half, getY(), getZ() + offset.z + z * half);
                    if (!level().getFluidState(pos).is(FluidTags.WATER)
                            && !level().getFluidState(pos.below()).is(FluidTags.WATER))
                        return false;
                }
            }
            return true;
        }

        /// 岸边必须有支撑和足够空间，并且能生成实际路径；允许跳上一格高的岸。
        private boolean tryLeaveWater() {
            for (int turn = 0; turn < 8; turn++) {
                Vec3 direction = swimDirection.yRot(turn * Mth.PI / 4);
                BlockPos ahead = BlockPos.containing(position().add(direction.scale(0.9)));
                for (int height = 0; height <= 1; height++) {
                    BlockPos feet = ahead.above(height);
                    if (!level().getBlockState(feet.below()).isFaceSturdy(level(), feet.below(), Direction.UP)
                            || !level().getFluidState(feet).isEmpty()) continue;
                    Vec3 destination = Vec3.atBottomCenterOf(feet);
                    if (!level().noCollision(Duck.this, getBoundingBox().move(destination.subtract(position()))))
                        continue;
                    var path = navigation.createPath(feet, 0);
                    if (path == null || !path.canReach()) continue;
                    changeMode(MovementMode.LAND);
                    if (navigation.moveTo(path, 1.0)) {
                        shoreTicks = 60;
                        getJumpControl().jump();
                        return true;
                    }
                    changeMode(MovementMode.WATER);
                }
            }
            return false;
        }

        /// 起飞后只完成一段上升、下降过程，不追着脚下地面反复爬升。
        private void tickFlight() {
            if (horizontalCollision) {
                flightDirection = flightDirection.yRot(Mth.HALF_PI);
            }
            double vertical = getDeltaMovement().y;
            if (mode == MovementMode.FLYING) {
                vertical -= 0.012;
                if (--flightTicks <= 0 || vertical <= 0.0 || verticalCollision && vertical > 0.0) {
                    changeMode(MovementMode.LANDING);
                    vertical = Math.min(0.0, vertical);
                }
            } else {
                vertical = Math.max(-0.30, vertical - 0.03);
            }
            setDeltaMovement(flightDirection.x * FLIGHT_SPEED, vertical, flightDirection.z * FLIGHT_SPEED);
            hasImpulse = true;
        }

        private void stopAction() {
            if (active != null) {
                active.stop();
                active = null;
            }
            navigation.stop();
        }

        @Override
        public void stop() {
            reset();
        }

        private void reset() {
            stopAction();
            flightTicks = 0;
            flightDirection = Vec3.ZERO;
            flightRequested = false;
            threat = null;
            flewThisFall = false;
            flightCooldown = 0;
            swimDirection = Vec3.ZERO;
            shoreTicks = 0;
            swimPauseTicks = 0;
            mode = MovementMode.LAND;
            setMovementMode(mode);
        }
    }

    /// 鸭子的两种基础外观。枚举同时承担同步值、持久化值和纹理路径的解析。
    public enum Variant implements IVariant {
        MALLARD("duck_1"),
        COMMON("duck_2");

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::ordinal, values(), MALLARD);
        private final String textureName;

        Variant(String textureName) {
            this.textureName = textureName;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/duck");
        }

        @Override
        public ResourceLocation texturePath() {
            return IVariant.resource("textures/entity/animal/duck/" + textureName + ".png");
        }

        @Override
        public Codec<Variant> codec() {
            return CODEC;
        }

        @Override
        public String serializeKey() {
            return VARIANT_KEY;
        }
    }
}
