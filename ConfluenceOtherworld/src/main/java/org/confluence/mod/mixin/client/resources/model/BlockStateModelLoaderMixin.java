package org.confluence.mod.mixin.client.resources.model;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terraentity.TerraEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
    @Unique
    private static final Set<String> confluence$skipSet = Set.of(Confluence.MODID, TerraFurniture.MODID, TerraEntity.MODID);

    /// "Exception loading blockstate definition: '{}' missing model for variant: '{}'"
    @WrapWithCondition(method = "lambda$loadBlockStateDefinitions$10", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 0))
    private static boolean skipConfluenceLog(Logger instance, String s, Object o1, Object o2) {
        return !(o1 instanceof ResourceLocation rl) || !confluence$skipSet.contains(rl.getNamespace());
    }
}
