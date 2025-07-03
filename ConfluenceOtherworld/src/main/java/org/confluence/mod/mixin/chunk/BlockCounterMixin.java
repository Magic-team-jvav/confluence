package org.confluence.mod.mixin.chunk;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IChunkSection;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.level.chunk.LevelChunkSection$1BlockCounter")
public abstract class BlockCounterMixin {
    @Unique
    IChunkSection confluence$section;

    @Dynamic // 抑制一下报错
    @Inject(method = "accept", at = @At("RETURN"))
    private void accept(BlockState state, int count, CallbackInfo ci) {
        if (confluence$section == null) return;
        if (state.getBlock() instanceof ISpreadable spreadable) {
            switch (spreadable.getSpreadType()) {
                case CRIMSON -> confluence$section.confluence$countCrimson(count);
                case CORRUPT -> confluence$section.confluence$countCorrupt(count);
                case HALLOW -> confluence$section.confluence$countHallow(count);
            }
        }
        if (state.is(ModTags.Blocks.TOMBSTONE)) {
            confluence$section.confluence$countTomb(count);
        } else if (state.is(Blocks.SUNFLOWER)) {
            confluence$section.confluence$countSunflower(count);
        }
    }

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void constr(LevelChunkSection section, CallbackInfo ci) {
        this.confluence$section = (IChunkSection) section;
    }
}
