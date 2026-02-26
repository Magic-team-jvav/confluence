package org.confluence.mod.mixin.client.model;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terraentity.TerraEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Unique
    private static final Set<String> confluence$skipSet = Set.of(Confluence.MODID, TerraFurniture.MODID, TerraEntity.MODID, TerraGuns.MODID);

    /// "Unable to load model: '{}' referenced from: {}: {}"
    @WrapWithCondition(method = "getModel", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private boolean skipConfluenceLog(Logger instance, String s, Object[] objects) {
        return objects.length == 0 || !(objects[0] instanceof ResourceLocation rl) || !confluence$skipSet.contains(rl.getNamespace());
    }
}
