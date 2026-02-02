package org.confluence.terraentity.entity.animal;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IVanillaVariant;
import org.confluence.terraentity.init.TESounds;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Map;

public class Duck extends Chicken implements GeoEntity , IVanillaVariant<Integer> {


    public static final String VARIANT_KEY = "Variant";

    public Duck(EntityType<? extends Duck> entityType, Level level) {
        super(entityType, level);
        this.getAttribute(Attributes.WATER_MOVEMENT_EFFICIENCY).setBaseValue(1);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.isInWater() && level().isClientSide && tickCount % 16 == 0){
            level().addParticle(ParticleTypes.BUBBLE_POP, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.FISHES);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return TESounds.ROUTINE_HURT.get();
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return TESounds.ROUTINE_DEATH.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Swim/Idle/Move", 5, state -> {
            if(this.isInWater()){
                return state.setAndContinue(DefaultAnimations.SWIM);
            }
            return state.setAndContinue(state.isMoving() ? DefaultAnimations.WALK : DefaultAnimations.IDLE);
        }));
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Duck.class, EntityDataSerializers.INT);


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, random.nextInt(getTexturesMap().size()));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_VARIANT_ID) {
            this.setVariant(this.entityData.get(DATA_VARIANT_ID));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt(VARIANT_KEY, this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(pCompound.getInt(VARIANT_KEY));
    }

    public static final int MALLARD_ID = 0;
    public static final int COMMON_ID = 1;
    static final Map<Integer, ResourceLocation> textures = new Int2ObjectOpenHashMap<>(Map.of(
            MALLARD_ID, TerraEntity.space("textures/entity/animal/duck/duck_1.png"),
            COMMON_ID, TerraEntity.space("textures/entity/animal/duck/duck_2.png")
    ));

    @Override
    public void setVariant(@NotNull Integer integer) {
        this.entityData.set(DATA_VARIANT_ID, integer);
    }

    @Override
    public @NotNull Integer getVariant() {
        return this.entityData.get(DATA_VARIANT_ID);
    }


    @Override
    public Map<Integer, ResourceLocation> getTexturesMap() {
        return textures;
    }
}
