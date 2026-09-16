package org.confluence.mod.common.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IForgeShearable;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.item.MaterialItems;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public final class Cluckshroom extends Chicken implements GeoEntity, IForgeShearable {
    private static final EntityDataAccessor<Boolean> BROWN = SynchedEntityData.defineId(Cluckshroom.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final boolean glowing;
    private UUID lastLightning;

    public Cluckshroom(EntityType<? extends Chicken> type, Level level, boolean glowing) {
        super(type, level);
        this.glowing = glowing;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BROWN, false);
    }

    public boolean isBrown() {
        return entityData.get(BROWN);
    }

    public boolean isGlowing() {
        return glowing;
    }

    @Override
    protected Component getTypeName() {
        return !glowing && isBrown() ? Component.translatable("entity.confluence.brown_cluckshroom") : super.getTypeName();
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt bolt) {
        if (glowing) {
            super.thunderHit(level, bolt);
        } else if (!bolt.getUUID().equals(lastLightning)) {
            entityData.set(BROWN, !isBrown());
            lastLightning = bolt.getUUID();
        }
    }

    @Override
    public Chicken getBreedOffspring(ServerLevel level, AgeableMob other) {
        Cluckshroom child = (glowing ? CritterEntities.GLOWING_CLUCKSHROOM : CritterEntities.CLUCKSHROOM).get().create(level);
        if (child != null) child.entityData.set(BROWN, isBrown());
        return child;
    }

    @Override
    public boolean isShearable(ItemStack stack, Level level, BlockPos pos) {
        return isAlive() && !isBaby();
    }

    @Override
    public List<ItemStack> onSheared(Player player, ItemStack stack, Level level, BlockPos pos, int fortune) {
        if (level.isClientSide || !isShearable(stack, level, pos)) return List.of();
        Chicken chicken = convertTo(EntityType.CHICKEN, false);
        if (chicken == null) return List.of();
        chicken.setHealth(Math.min(getHealth(), chicken.getMaxHealth()));
        chicken.eggTime = eggTime;
        level.playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
        return List.of(new ItemStack(glowing ? MaterialItems.GLOWING_MUSHROOM.get() : isBrown() ? Items.BROWN_MUSHROOM : Items.RED_MUSHROOM, 3));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Brown", isBrown());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(BROWN, tag.getBoolean("Brown"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
}
