package org.confluence.mod.mixin.client.resources.model;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_guns.TerraGuns;
import org.confluence.terraentity.TerraEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {
    @Unique
    private static final Set<String> confluence$skipSet = Set.of(Confluence.MODID, TerraFurniture.MODID, TerraEntity.MODID, TerraGuns.MODID);

    /// "Missing textures in model {}:\n{}"
    @WrapWithCondition(method = "lambda$loadModels$17",at= @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private static boolean skipConfluenceLog(Logger instance, String s, Object o1, Object o2) {
        return !(o1 instanceof ModelResourceLocation rl) || !confluence$skipSet.contains(rl.id().getNamespace());
    }
}
