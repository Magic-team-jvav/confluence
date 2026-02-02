package org.confluence.terraentity.entity.animal;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IVanillaVariant;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Map;

public class Squirrel extends Animal implements GeoEntity, IVanillaVariant<Integer> {

    public static final String VARIANT_KEY = "Variant";
    private boolean initializedVariant = false;

    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Squirrel.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public Squirrel(EntityType<? extends Squirrel> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public boolean isFood(ItemStack stack) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.SAFE_FALL_DISTANCE, 6).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
    }

    protected SoundEvent getAmbientSound() {
        return null;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return TESounds.ROUTINE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return TESounds.ROUTINE_DEATH.get();
    }

    protected float getSoundVolume() {
        return 0.4F;
    }


    @Nullable
    public Squirrel getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return TEAnimals.SQUIRREL.get().create(level);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
    }


    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (!level().isClientSide && !initializedVariant) {
            this.setVariant(random.nextInt(getTexturesMap().size()));
        }
    }

    @Override
    public void setVariant(@NotNull Integer integer) {
        this.entityData.set(DATA_VARIANT_ID, integer);
    }

    @Override
    public @NotNull Integer getVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt(VARIANT_KEY, this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains(VARIANT_KEY)) {
            this.setVariant(pCompound.getInt(VARIANT_KEY));
            this.initializedVariant = true;
        }
    }

    public static final int COMMON_ID = 0;
    public static final int RED_ID = 1;
    static Map<Integer, ResourceLocation> textures = new Int2ObjectOpenHashMap<>(ImmutableMap.<Integer, ResourceLocation>builder()
            .put(COMMON_ID, TerraEntity.space("textures/entity/animal/squirrel/squirrel.png"))
            .put(RED_ID, TerraEntity.space("textures/entity/animal/squirrel/red_squirrel.png"))
            .build()
    );

    @Override
    public Map<Integer, ResourceLocation> getTexturesMap() {
        return textures;
    }
}
