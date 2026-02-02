package org.confluence.terraentity.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.entity.IVanillaVariant;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JumpableVariantAnimal extends JumpableAnimal implements IVanillaVariant<Integer> {

    public static final String VARIANT_KEY = "Variant";

    public JumpableVariantAnimal(EntityType<? extends JumpableAnimal> entityType, Level level, Map<Integer, ResourceLocation> texturesMap) {
        super(entityType, level);
        this.texturesMap = texturesMap;
    }

    Map<Integer, ResourceLocation> texturesMap;
    private boolean initializedVariant = false;
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(JumpableVariantAnimal.class, EntityDataSerializers.INT);


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

    @Override
    public Map<Integer, ResourceLocation> getTexturesMap() {
        return texturesMap;
    }

}
