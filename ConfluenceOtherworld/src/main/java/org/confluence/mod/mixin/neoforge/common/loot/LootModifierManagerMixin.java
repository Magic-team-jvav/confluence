package org.confluence.mod.mixin.neoforge.common.loot;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.LootModifierManager;
import org.confluence.terra_curio.TerraCurio;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = LootModifierManager.class, remap = false, priority = 900)
public abstract class LootModifierManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void removeTerraCurio(CallbackInfo ci, @Local(argsOnly = true) Map<ResourceLocation, JsonElement> resourceList) {
        resourceList.entrySet().removeIf(entry -> entry.getKey().getNamespace().equals(TerraCurio.MODID));
    }
}
