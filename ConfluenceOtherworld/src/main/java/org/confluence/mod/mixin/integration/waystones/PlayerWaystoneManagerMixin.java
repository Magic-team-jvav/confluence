package org.confluence.mod.mixin.integration.waystones;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "net.blay09.mods.waystones.core.PlayerWaystoneManager", remap = false)
public abstract class PlayerWaystoneManagerMixin {
    @WrapOperation(method = "activateWaystone",at= @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;equals(Ljava/lang/Object;)Z"))
    private static boolean additionalCheck(ResourceLocation instance, Object resourcelocation, Operation<Boolean> original) {
        return original.call(instance, resourcelocation) || Confluence.MODID.equals(instance.getNamespace());
    }
}
