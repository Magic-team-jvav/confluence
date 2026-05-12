package org.confluence.mod.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin {
    @Inject(method = "isNearWater", at = @At("HEAD"), cancellable = true)
    private static void hasRainBlock(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockPos.MutableBlockPos mutable = pos.mutable().move(0, 1, 0);
        while (mutable.getY() - pos.getY() < 16 && level.getBlockState(mutable).isAir()) {
            mutable.move(0, 1, 0);
        }
        BlockState blockState = level.getBlockState(mutable);
        if (blockState.is(NatureBlocks.RAIN_CLOUD_BLOCK)) {
            cir.setReturnValue(true);
        }
    }
}
