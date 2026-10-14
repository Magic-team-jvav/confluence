package org.confluence.mod.common.entity.animal;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
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

// 鸭子独立管理陆地巡游、水面游泳、受惊起飞和降落。
public class Duck extends BaseCritter implements FlyingAnimal, VariantHolder<Duck.Variant> {
    public static final String VARIANT_KEY = "Variant";
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
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
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this) {
            @Override
            public void clientTick() {
                // 身体与视线使用同一偏航，避免客户端实体转向和头部同步产生偏差。
                setYBodyRot(getYHeadRot());
            }
        };
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    private void setMovementMode(MovementMode mode) {
        MoveControl nextControl = switch (mode) {
            case LAND, WATER -> groundControl;
            case FLYING, LANDING -> flightControl;
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
        setNoGravity(mode == MovementMode.WATER || mode == MovementMode.FLYING);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_VARIANT, Variant.MALLARD.ordinal());
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

    @Override
    public boolean isFlying() {
        return !onGround() && !isInWater();
    }

    @Override
    public void tick() {
        // NoAI 时行为树不运行，仍需解除可能残留的飞行控制。
        if (!level().isClientSide && isNoAi() && behavior != null) behavior.reset();
        super.tick();
        if (!level().isClientSide && !isNoAi()) {
            // 将注视偏航同步到实体和身体，保持整身朝向一致。
            boolean flying = behavior != null && (behavior.mode == MovementMode.FLYING
                    || behavior.mode == MovementMode.LANDING);
            float yaw;
            if (flying && behavior.flightDirection.horizontalDistanceSqr() > 1.0E-6) {
                Vec3 direction = behavior.flightDirection;
                yaw = (float) (Mth.atan2(-direction.x, direction.z) * Mth.RAD_TO_DEG);
                setXRot(0.0F);
            } else {
                yaw = navigation.isDone() ? getYHeadRot() : getYRot();
            }
            setYRot(yaw);
            setYBodyRot(yaw);
            setYHeadRot(yaw);
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
        controllers.add(new AnimationController<>(this, "Swim/Idle/Move", 5, state -> {
            if (isInWater()) {
                return state.setAndContinue(SWIM);
            }
            return state.setAndContinue(state.isMoving() ? WALK : IDLE);
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

    @Override
    public void travel(Vec3 input) {
        if (isEffectiveAi() && isInWater() && !isNoAi() && behavior != null && behavior.mode == MovementMode.WATER) {
            moveRelative(getSpeed(), input);
            double targetY = waterSurface() - getBbHeight() * 0.35;
            Vec3 movement = getDeltaMovement();
            double vertical = Mth.clamp((targetY - getY()) * 0.2, -0.08, 0.08);
            setDeltaMovement(movement.x, vertical, movement.z);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().multiply(0.9, 0.0, 0.9));
            calculateEntityAnimation(false);
        } else super.travel(input);
    }

    private double waterSurface() {
        BlockPos pos = blockPosition();
        if (!level().getFluidState(pos).is(FluidTags.WATER)) pos = pos.below();
        for (int i = 0; i < 8 && level().getFluidState(pos.above()).is(FluidTags.WATER); i++) {
            pos = pos.above();
        }
        return pos.getY() + level().getFluidState(pos).getHeight(level(), pos);
    }

    private enum MovementMode {
        LAND, WATER, FLYING, LANDING
    }

    // 管理巡游、游泳、受惊起飞和降落，并在状态切换时结束上一动作。
    private final class DuckBehavior extends BTNode {
        private final BTNode landRoutine = routine(new WaterAvoidingRandomStrollGoal(Duck.this, 1.0D));
        private final BTNode waterRoutine = routine(new RandomSwimmingGoal(Duck.this, 1.0D, 30));
        private MovementMode mode = MovementMode.LAND;
        private BTNode active;
        private Vec3 flightDirection = Vec3.ZERO;
        private int flightTicks;
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
            if (!airborne()) {
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
        }

        private void updateEnvironment() {
            boolean inWater = isInWater();
            if (onGround() || inWater) flewThisFall = false;
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
            updateEnvironment();
            if (mode == MovementMode.WATER && !flightRequested) {
                Player player = level().getNearestPlayer(Duck.this, 3.0);
                if (player != null && !player.isSpectator()) requestFlight(player);
            }
            if (flightRequested || mode == MovementMode.LAND && !onGround()
                    && !flewThisFall && fallDistance >= 3.0F) beginFlight();
            if (airborne()) {
                tickFlight();
                return BTStatus.RUNNING;
            }
            if (active == null) {
                active = mode == MovementMode.WATER ? waterRoutine : landRoutine;
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
            flightTicks = 100;
            changeMode(MovementMode.FLYING);
            setDeltaMovement(flightDirection.scale(0.2).add(0.0, 0.25, 0.0));
            hasImpulse = true;
        }

        private void tickFlight() {
            if (mode == MovementMode.FLYING && --flightTicks <= 0) {
                changeMode(MovementMode.LANDING);
            }
            Vec3 movement = getDeltaMovement();
            if (horizontalCollision) flightDirection = flightDirection.scale(-1.0);
            double vertical = movement.y;
            if (mode == MovementMode.FLYING) {
                // 根据前下方的地面和水面调整飞行高度。
                BlockPos ahead = BlockPos.containing(position().add(flightDirection).add(0.0, -0.5, 0.0));
                double desired = -0.12;
                for (int depth = 0; depth < 8; depth++) {
                    BlockPos pos = ahead.below(depth);
                    if (!level().hasChunkAt(pos)) break;
                    if (!level().getBlockState(pos).getCollisionShape(level(), pos).isEmpty() || level().getFluidState(pos).is(FluidTags.WATER)) {
                        desired = depth < 3 ? 0.16 : -0.08;
                        break;
                    }
                }
                vertical += Mth.clamp(desired - vertical, -0.02, 0.02);
                if (verticalCollision && vertical > 0.0) vertical = -0.08;
            }
            setDeltaMovement(flightDirection.x * 0.2, vertical, flightDirection.z * 0.2);
            getLookControl().setLookAt(getX() + flightDirection.x, getEyeY(), getZ() + flightDirection.z);
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
            mode = MovementMode.LAND;
            setMovementMode(mode);
        }
    }

    // 鸭子的两种基础外观。枚举同时承担同步值、持久化值和纹理路径的解析。
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
