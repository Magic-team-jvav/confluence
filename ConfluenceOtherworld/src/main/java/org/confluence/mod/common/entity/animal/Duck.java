package org.confluence.mod.common.entity.animal;

import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.Locale;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.minecraft.world.entity.ai.goal.*;


/// 鸭子具有独立的水陆巡游与受惊飞行行为，不继承鸡的繁殖和产蛋逻辑。
public class Duck extends BaseCritter implements FlyingAnimal, VariantHolder<Duck.Variant> {
    public static final String VARIANT_KEY = "Variant";
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    private static final String ESCAPE_FLIGHT_TICKS_KEY = "EscapeFlightTicks";
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Duck.class, EntityDataSerializers.INT);
    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.MALLARD, 1)
            .add(Variant.COMMON, 1)
            .build();
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int escapeFlightTicks;

    public Duck(EntityType<? extends Duck> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 10, false);
        getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(1.0);
        setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new DuckEscapeFlightGoal(Duck.this)),
                        new VanillaGoalAction(new FloatGoal(Duck.this)),
                        new VanillaGoalAction(new PanicGoal(Duck.this, 1.3D)),
                        new VanillaGoalAction(new RandomSwimmingGoal(Duck.this, 1.0D, 30)),
                        new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(Duck.this, 1.0D)),
                        new VanillaGoalAction(new LookAtPlayerGoal(Duck.this, Player.class, 6.0F)),
                        new VanillaGoalAction(new RandomLookAroundGoal(Duck.this))
                );
            }
        };
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanFloat(true);
        navigation.setCanOpenDoors(false);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, Variant.MALLARD.ordinal());
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
        tag.putInt(ESCAPE_FLIGHT_TICKS_KEY, escapeFlightTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.MALLARD);
        } else {
            Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)).ifSuccess(this::setVariant);
        }
        escapeFlightTicks = Math.max(0, tag.getInt(ESCAPE_FLIGHT_TICKS_KEY));
        setNoGravity(escapeFlightTicks > 0);
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
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isFlying() {
        return !onGround() && !isInWater();
    }

    @Override
    public void tick() {
        super.tick();
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
            if (isFlying()) {
                return state.setAndContinue(IDLE);
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

    /// 鸭子只会在水中受到附近玩家惊扰时起飞；进入飞行后短暂沿远离玩家的方向移动，
    /// 计时结束即恢复重力并自行落地，不借用鸟类的树冠巡游和跟群行为。
    private static final class DuckEscapeFlightGoal extends Goal {
        private static final double TRIGGER_RANGE = 5.0;
        private static final double FLIGHT_DISTANCE = 8.0;
        private final Duck duck;
        private Player threat;
        private int repathTicks;

        private DuckEscapeFlightGoal(Duck duck) {
            this.duck = duck;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (duck.escapeFlightTicks > 0) return true;
            if (!duck.isInWater()) return false;
            threat = duck.level().getNearestPlayer(duck, TRIGGER_RANGE);
            return threat != null && !threat.isSpectator();
        }

        @Override
        public boolean canContinueToUse() {
            return duck.escapeFlightTicks > 0;
        }

        @Override
        public void start() {
            if (duck.escapeFlightTicks <= 0) duck.escapeFlightTicks = 60 + duck.random.nextInt(61);
            duck.setNoGravity(true);
            duck.setDeltaMovement(duck.getDeltaMovement().add(0.0, 0.25, 0.0));
            repathTicks = 0;
        }

        @Override
        public void tick() {
            if (--duck.escapeFlightTicks <= 0) return;
            if (--repathTicks > 0 && !duck.getNavigation().isDone()) return;
            if (threat == null || !threat.isAlive())
                threat = duck.level().getNearestPlayer(duck, TRIGGER_RANGE * 2.0);
            Vec3 away = threat == null ? duck.getLookAngle().multiply(1.0, 0.0, 1.0)
                    : duck.position().subtract(threat.position()).multiply(1.0, 0.0, 1.0);
            if (away.lengthSqr() < 1.0E-6) {
                double angle = duck.random.nextDouble() * Math.PI * 2.0;
                away = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
            } else {
                away = away.normalize();
            }
            Vec3 destination = duck.position().add(away.scale(FLIGHT_DISTANCE)).add(0.0, 2.0 + duck.random.nextDouble() * 2.0, 0.0);
            duck.getNavigation().moveTo(destination.x, destination.y, destination.z, 1.3);
            repathTicks = 20;
        }

        @Override
        public void stop() {
            duck.escapeFlightTicks = 0;
            duck.setNoGravity(false);
            duck.getNavigation().stop();
            threat = null;
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
