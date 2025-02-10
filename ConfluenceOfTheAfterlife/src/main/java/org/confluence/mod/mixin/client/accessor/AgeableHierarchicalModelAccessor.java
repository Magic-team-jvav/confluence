package org.confluence.mod.mixin.client.accessor;

import net.minecraft.client.model.AgeableHierarchicalModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AgeableHierarchicalModel.class)
public interface AgeableHierarchicalModelAccessor {
    @Accessor
    float getYoungScaleFactor();

    @Accessor
    float getBodyYOffset();
}
