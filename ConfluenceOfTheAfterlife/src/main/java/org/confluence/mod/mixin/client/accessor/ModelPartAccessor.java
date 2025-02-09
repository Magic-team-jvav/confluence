package org.confluence.mod.mixin.client.accessor;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {
    @Accessor
    List<ModelPart.Cube> getCubes();

    @Accessor
    Map<String, ModelPart> getChildren();
}
