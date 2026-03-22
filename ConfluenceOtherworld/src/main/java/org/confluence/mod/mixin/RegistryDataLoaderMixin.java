package org.confluence.mod.mixin;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.serialization.Decoder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.confluence.mod.common.worldgen.TheEndBiomeHolder;
import org.confluence.terraentity.TerraEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {
    @Inject(method = "loadContentsFromManager", at = @At("HEAD"))
    private static <E> void cache(CallbackInfo ci, @Local(argsOnly = true) WritableRegistry<E> registry, @Share("isNotBiomeModifier") LocalBooleanRef isNotBiomeModifier) {
        isNotBiomeModifier.set(!registry.key().equals(NeoForgeRegistries.Keys.BIOME_MODIFIERS));
    }

    @WrapWithCondition(method = "loadContentsFromManager", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryDataLoader;loadElementFromResource(Lnet/minecraft/core/WritableRegistry;Lcom/mojang/serialization/Decoder;Lnet/minecraft/resources/RegistryOps;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/server/packs/resources/Resource;Lnet/minecraft/core/RegistrationInfo;)V"))
    private static <E> boolean remove(WritableRegistry<E> jsonelement, Decoder<E> dataresult, RegistryOps<JsonElement> candidate, ResourceKey<E> reader, Resource registry, RegistrationInfo codec, @Share("isNotBiomeModifier") LocalBooleanRef isNotBiomeModifier) {
        return isNotBiomeModifier.get() || !reader.location().getNamespace().equals(TerraEntity.MODID);
    }

    @Mixin(RegistryDataLoader.RegistryData.class)
    public abstract static class RegistryDataMixin<T> {
        @Shadow
        @Final
        private ResourceKey<? extends Registry<T>> key;

        @Inject(method = "create", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
        private void modifyRegistry(CallbackInfoReturnable<Object> cir, @Local RegistryBuilder<T> builder) {
            if (Registries.DIMENSION_TYPE.equals(key)) {
                builder.onAdd((registry, id, key, value) -> {
                    if (BuiltinDimensionTypes.END.equals(key)) {
                        TheEndBiomeHolder.modifyDimensionType((DimensionType) value);
                    }
                });
            }
        }
    }
}
