package org.confluence.mod.mixin.world.level.levelgen.feature;

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
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.confluence.mod.util.OverworldUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(PlacedFeature.class)
public abstract class PlacedFeatureMixin {
    @Shadow
    @Final
    private Holder<ConfiguredFeature<?, ?>> feature;

    @Unique
    private PortTriState confluence$isPine = PortTriState.DEFAULT;

    @ModifyArg(method = "placeWithContext", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private Consumer<BlockPos> wrap(
            Consumer<BlockPos> consumer,
            @Local(argsOnly = true) PlacementContext context,
            @Local(argsOnly = true) RandomSource source,
            @Local MutableBoolean success
    ) {
        if (confluence$isPine == PortTriState.FALSE) { // 大概率为false，所以只需要检查一次
            return consumer;
        }

        if (confluence$isPine == PortTriState.TRUE) { // 不是false，大概率就是true
            return pos -> {
                if (OverworldUtils.replacePine(context, source, pos)) {
                    success.setTrue();
                } else {
                    consumer.accept(pos);
                }
            };
        }

        if (confluence$isPine == PortTriState.DEFAULT) check:{ // 小概率为default
            if (feature.value().feature() instanceof TreeFeature) {
                ResourceLocation id = feature.unwrapKey().map(ResourceKey::location).orElse(null);
                if (id != null && id.getPath().equals("pine") && id.getNamespace().equals("minecraft")) {
                    this.confluence$isPine = PortTriState.TRUE;
                    break check;
                }
            }
            this.confluence$isPine = PortTriState.FALSE;
        }

        return consumer; // 放过这一次替换
    }
}
