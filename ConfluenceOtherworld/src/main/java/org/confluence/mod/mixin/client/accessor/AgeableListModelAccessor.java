package org.confluence.mod.mixin.client.accessor;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AgeableListModel.class)
public interface AgeableListModelAccessor {
    @Accessor
    boolean getScaleHead();

    @Accessor
    float getBabyYHeadOffset();

    @Accessor
    float getBabyZHeadOffset();

    @Accessor
    float getBabyHeadScale();

    @Accessor
    float getBabyBodyScale();

    @Accessor
    float getBodyYOffset();

    @Invoker
    Iterable<ModelPart> callHeadParts();

    @Invoker
    Iterable<ModelPart> callBodyParts();
}
