package org.confluence.mod.mixin.level;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.neoforged.neoforge.common.util.TriState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.confluence.mod.util.OverworldUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(PlacedFeature.class)
public abstract class PlacedFeatureMixin {
    @Shadow
    @Final
    private Holder<ConfiguredFeature<?, ?>> feature;

    @Unique
    private TriState confluence$isPine = TriState.DEFAULT;

    @WrapOperation(method = "placeWithContext", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private void wrap(
            Stream<BlockPos> instance,
            Consumer<BlockPos> consumer,
            Operation<Void> original,
            @Local(argsOnly = true) PlacementContext context,
            @Local(argsOnly = true) RandomSource source,
            @Local MutableBoolean success
    ) {
        if (confluence$isPine.isDefault()) check:{
            if (feature.value().feature() instanceof TreeFeature) {
                ResourceLocation id = feature.unwrapKey().map(ResourceKey::location).orElse(null);
                if (id != null && id.getPath().equals("pine") && id.getNamespace().equals("minecraft")) {
                    this.confluence$isPine = TriState.TRUE;
                    break check;
                }
            }
            this.confluence$isPine = TriState.FALSE;
        }

        if (confluence$isPine.isTrue()) {
            original.call(instance, (Consumer<BlockPos>) pos -> {
                if (OverworldUtils.replacePine(context, source, pos)) {
                    success.setTrue();
                } else {
                    consumer.accept(pos);
                }
            });
        } else {
            original.call(instance, consumer);
        }
    }
}
