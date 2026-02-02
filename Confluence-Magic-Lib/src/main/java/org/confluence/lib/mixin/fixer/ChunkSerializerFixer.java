package org.confluence.lib.mixin.fixer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.confluence.lib.common.data.IdFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerFixer {
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/PalettedContainer;codecRW(Lnet/minecraft/core/IdMap;Lcom/mojang/serialization/Codec;Lnet/minecraft/world/level/chunk/PalettedContainer$Strategy;Ljava/lang/Object;)Lcom/mojang/serialization/Codec;"), index = 1)
    private static <T> Codec<T> fixBlockStateName(Codec<T> codec) {
        return codec.mapResult(IdFixer.fixBlockName(codec));
    }

    @ModifyExpressionValue(method = "makeBiomeCodec",at= @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;holderByNameCodec()Lcom/mojang/serialization/Codec;"))
    private static Codec<Holder<Biome>> fixBiomeName(Codec<Holder<Biome>> codec) {
        return codec.mapResult(IdFixer.fixBiomeName(codec));
    }
}
