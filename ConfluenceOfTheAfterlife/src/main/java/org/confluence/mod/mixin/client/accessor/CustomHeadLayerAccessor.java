package org.confluence.mod.mixin.client.accessor;

import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.level.block.SkullBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CustomHeadLayer.class)
public interface CustomHeadLayerAccessor {
    @Accessor
    Map<SkullBlock.Type, SkullModelBase> getSkullModels();
}
