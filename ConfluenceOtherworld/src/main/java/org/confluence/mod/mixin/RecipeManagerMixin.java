package org.confluence.mod.mixin;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.*;
import org.confluence.mod.integration.terra_curio.TCRemoval;
import org.confluence.mod.mixed.IRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 900)
public abstract class RecipeManagerMixin implements IRecipeManager {
    @Shadow protected abstract <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(RecipeType<T> type);

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> confluence$byType(RecipeType<T> type) {
        return byType(type);
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void removeTerraCurio(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        for (ResourceLocation recipe : TCRemoval.RECIPES) {
            object.remove(recipe);
        }
    }
}
