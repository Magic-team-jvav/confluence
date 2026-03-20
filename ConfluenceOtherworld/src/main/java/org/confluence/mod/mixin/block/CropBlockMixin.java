package org.confluence.mod.mixin.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {
    @Shadow
    @Final
    public static IntegerProperty AGE;

    @Shadow
    @Final
    public static int MAX_AGE;

    @Inject(method = "entityInside", at = @At("TAIL"))
    private void explode(CallbackInfo ci, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        if (state.is(Blocks.POTATOES) && state.getValue(AGE) == MAX_AGE && level instanceof ServerLevel serverLevel && ModSecretSeeds.NO_TRAPS.match(serverLevel)) {
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 2.5F, false, Level.ExplosionInteraction.BLOCK);
        }
    }
}
