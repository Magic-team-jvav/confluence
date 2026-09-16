package org.confluence.mod.common.entity.animal;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;

import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.gameevent.BloodMoonGameEvent;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Locale;
import java.util.function.IntFunction;

/// 使用原版 {@link Rabbit} 的导航、跳跃控制和行为，只扩展本模组的外观与待机动画。
public class Bunny extends Rabbit implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Bunny.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WATCH_STATE = SynchedEntityData.defineId(Bunny.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";
    private static final RawAnimation WATCH_1 = RawAnimation.begin().thenPlay("watch_1");
    private static final RawAnimation WATCH_2 = RawAnimation.begin().thenPlay("watch_2");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.NORMAL, 399)
            .add(Variant.GOLD, 1)
            .build();

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int idleTicks;
    private int nextWatchTick;
    private int watchTicksRemaining = 20;
    private int watchAnimationType;

    public Bunny(EntityType<? extends Bunny> type, Level level) {
        super(type, level);
    }

    public Variant getBunnyVariant() {
        return Variant.BY_ID.apply(entityData.get(DATA_VARIANT));
    }

    public void setBunnyVariant(Variant variant) {
        entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_VARIANT, Variant.NORMAL.ordinal());
        entityData.define(DATA_WATCH_STATE, 20);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        if (tag == null || !tag.contains(VARIANT_KEY)) initializeSpawnVariant();
        return result;
    }

    protected void initializeSpawnVariant() {
        setBunnyVariant(SPAWN_VARIANTS.select(random));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && tickCount % 5 == 0 && getBunnyVariant() == Variant.GOLD) {
            level().addParticle(ParticleTypes.ELECTRIC_SPARK, getRandomX(0.8), getRandomY(), getRandomZ(0.8), 0.0, 0.01, 0.0);
        }
        --watchTicksRemaining;
        if (level().isClientSide) return;
        if (isAlive() && getBunnyVariant() == Variant.NORMAL && tickCount % 20 == 0 && BloodMoonGameEvent.INSTANCE.started()) {
            corrupt(CritterCorruption.selectsCrimson(this));
            return;
        }
        if (!navigation.isDone()) {
            idleTicks = 0;
            if (watchTicksRemaining >= 0) {
                watchTicksRemaining = -1;
                entityData.set(DATA_WATCH_STATE, -1);
            }
        } else {
            ++idleTicks;
            if (idleTicks % 100 == 99) nextWatchTick = tickCount + random.nextInt(20);
            if (tickCount == nextWatchTick) beginWatchCycle(50 + 1000 * random.nextInt(2));
        }
    }

    public void corrupt(boolean crimson) {
        if (!level().isClientSide && isAlive() && getBunnyVariant() == Variant.NORMAL) {
            HostileBunny converted = convertTo(CritterEntities.HOSTILE_BUNNY.get(), false);
            if (converted != null)
                converted.setBunnyVariant(crimson ? Variant.VICIOUS : Variant.CORRUPT);
        }
    }

    private void beginWatchCycle(int encodedDuration) {
        watchAnimationType = encodedDuration / 1000;
        watchTicksRemaining = encodedDuration % 1000;
        entityData.set(DATA_WATCH_STATE, encodedDuration);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_WATCH_STATE.equals(key)) {
            int packedState = entityData.get(DATA_WATCH_STATE);
            watchAnimationType = packedState / 1000;
            if (level().isClientSide) watchTicksRemaining = packedState % 1000;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getBunnyVariant().serialize(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setBunnyVariant(Variant.NORMAL);
            return;
        }
        PortDataResultExtension.ifSuccess(Variant.CODEC.decode(NbtOps.INSTANCE, tag.get(VARIANT_KEY)), pair -> setBunnyVariant(pair.getFirst()));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle/Move", 5, state -> {
            if (watchTicksRemaining > 0)
                return state.setAndContinue(watchAnimationType == 0 ? WATCH_1 : WATCH_2);
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
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.CARROT) || stack.is(Items.GOLDEN_CARROT) || stack.is(Items.DANDELION);
    }

    @Override
    public @Nullable Bunny getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return CritterEntities.BUNNY.get().create(level);
    }

    public enum Variant implements IVariant {
        NORMAL, GOLD, PARTY, SLIMED, XMAS,
        AMETHYST, TOPAZ, SAPPHIRE, EMERALD, RUBY, AMBER, DIAMOND,
        CORRUPT, VICIOUS, EXPLOSIVE;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::ordinal, values(), NORMAL);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/" + (this == EXPLOSIVE ? "explosive_bunny" : "bunny"));
        }

        @Override
        public ResourceLocation texturePath() {
            String name = switch (this) {
                case NORMAL, PARTY, SLIMED, XMAS, CORRUPT, VICIOUS -> "bunny";
                case GOLD -> "golden_bunny";
                case EXPLOSIVE -> "explosive_bunny";
                default -> getSerializedName() + "_bunny";
            };
            return IVariant.resource("textures/entity/animal/bunny/" + name + ".png");
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
