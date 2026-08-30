package org.confluence.mod.mixin.client.model.geom;

import net.minecraft.client.model.geom.ModelPart;
import org.confluence.mod.mixed.IModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ModelPart.class)
public class ModelPartMixin implements IModelPart {
    @Unique
    private ModelPart confluence$root;
    @Unique
    private boolean confluence$isSkull;

    @Override
    public ModelPart confluence$root(ModelPart... root) {
        if (root.length > 0) {
            confluence$root = root[0];
        }
        return confluence$root;
    }

    @Override
    public boolean confluence$isSkull(boolean... isSkull) {
        if (isSkull.length > 0) {
            confluence$isSkull = isSkull[0];
        }
        return confluence$isSkull;
    }
}
