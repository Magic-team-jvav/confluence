package org.confluence.mod.mixin.chunk;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 从硬盘加载区块的时候会重新数方块
 */
@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunkSection$1BlockCounter")
public abstract class BlockCounterMixin {
    @Unique
    private @Nullable ILevelChunkSection confluence$section;

    @Dynamic // 抑制一下报错
    @Inject(method = "accept", at = @At("RETURN"))
    private void accept(BlockState state, int count, CallbackInfo ci) {
        if (confluence$section == null) return;
        DynamicBiomeUtils.COUNTER.forEach((predicate, consumer) -> {
            if (predicate.test(state)) {
                consumer.accept(confluence$section.confluence$getBlockCounts(), count);
            }
        });
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void constr(LevelChunkSection section, CallbackInfo ci) {
        this.confluence$section = ILevelChunkSection.of(section);
    }
}
