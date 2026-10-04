package org.confluence.mod.mixin.resources;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.TheEndBiomeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryDataLoader.RegistryData.class)
public abstract class RegistryDataLoader$RegistryDataMixin<T> {
    @Shadow
    @Final
    private ResourceKey<? extends Registry<T>> key;

    @Inject(method = "create", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    private void modifyRegistry(CallbackInfoReturnable<Object> cir, @Local RegistryBuilder<T> builder) {
        if (Confluence.THE_END_BIOMES && Registries.DIMENSION_TYPE.equals(key)) {
            builder.onAdd((registry, id, key, value) -> {
                if (BuiltinDimensionTypes.END.equals(key)) {
                    TheEndBiomeHolder.modifyDimensionType((DimensionType) value);
                }
            });
        }
    }
}
