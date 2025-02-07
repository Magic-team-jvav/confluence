package org.confluence.mod.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
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
    private void explode(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (state.is(Blocks.POTATOES) && state.getValue(AGE) == MAX_AGE && level instanceof ServerLevel serverLevel && ModSecretSeeds.NO_TRAPS.match(serverLevel)) {
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), 2.5F, false, Level.ExplosionInteraction.BLOCK);
        }
    }
}
