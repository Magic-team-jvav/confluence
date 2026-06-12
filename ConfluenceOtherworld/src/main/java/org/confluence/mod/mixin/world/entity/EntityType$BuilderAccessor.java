package org.confluence.mod.mixin.world.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@Mixin(EntityType.Builder.class)
public interface EntityType$BuilderAccessor<T extends Entity> {
    @Accessor
    EntityType.EntityFactory<T> getFactory();

    @Accessor
    MobCategory getCategory();

    @Accessor
    ImmutableSet<Block> getImmuneTo();

    @Accessor
    boolean isSerialize();

    @Accessor
    boolean isSummon();

    @Accessor
    boolean isFireImmune();

    @Accessor
    boolean isCanSpawnFarFromPlayer();

    @Accessor
    int getClientTrackingRange();

    @Accessor
    int getUpdateInterval();

    @Accessor
    EntityDimensions getDimensions();

    @Accessor
    float getSpawnDimensionsScale();

    @Accessor
    EntityAttachments.Builder getAttachments();

    @Accessor
    FeatureFlagSet getRequiredFeatures();

    @Accessor
    Predicate<EntityType<?>> getVelocityUpdateSupplier();

    @Accessor
    ToIntFunction<EntityType<?>> getTrackingRangeSupplier();

    @Accessor
    ToIntFunction<EntityType<?>> getUpdateIntervalSupplier();
}
