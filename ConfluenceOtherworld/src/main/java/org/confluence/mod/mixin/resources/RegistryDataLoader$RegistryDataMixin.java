package org.confluence.mod.mixin.resources;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.TheEndBiomeHolder;
import org.mesdag.portlib.diff.IPortMappedRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegistryDataLoader.RegistryData.class)
public abstract class RegistryDataLoader$RegistryDataMixin<T> {
    @Shadow
    @Final
    private ResourceKey<? extends Registry<T>> key;

    @ModifyExpressionValue(method = "create", at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/MappedRegistry;"))
    private MappedRegistry<T> modifyRegistry(MappedRegistry<T> original) {
        if (Confluence.THE_END_BIOMES && Registries.DIMENSION_TYPE.equals(key)) {
            IPortMappedRegistry.of(original).onAdd((registry, id, key, value) -> {
                if (BuiltinDimensionTypes.END.equals(key)) {
                    TheEndBiomeHolder.modifyDimensionType((DimensionType) value);
                }
            });
        }
        return original;
    }
}
