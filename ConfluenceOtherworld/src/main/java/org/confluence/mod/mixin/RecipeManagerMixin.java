package org.confluence.mod.mixin;

import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.apache.commons.lang3.mutable.MutableObject;
import org.confluence.mod.integration.create.CreateHelper;
import org.confluence.mod.integration.terra_curio.TCHelper;
import org.confluence.mod.integration.terra_entity.TEHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = RecipeManager.class, priority = 900)
public abstract class RecipeManagerMixin {
    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void processRecipes(CallbackInfo ci, @Local(argsOnly = true) Map<ResourceLocation, JsonElement> recipes) {
        TCHelper.processRecipes(recipes);
        TEHelper.processRecipes(recipes);
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",at= @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void buildRecipes(CallbackInfo ci) {
        MutableObject<Map<ResourceLocation, RecipeHolder<?>>> byName1 = new MutableObject<>(byName);
        MutableObject<Multimap<RecipeType<?>, RecipeHolder<?>>> byType1 = new MutableObject<>(byType);
        CreateHelper.buildRecipes(byName1, byType1);
        this.byName = byName1.getValue();
        this.byType = byType1.getValue();
    }
}
