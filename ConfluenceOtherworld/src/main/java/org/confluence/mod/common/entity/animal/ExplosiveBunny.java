package org.confluence.mod.common.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ExplosiveBunny extends Bunny {
    public ExplosiveBunny(EntityType<? extends ExplosiveBunny> type, Level level) {
        super(type, level);
        setVariant(Variant.EXPLOSIVE);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(Variant.EXPLOSIVE);
    }
}
