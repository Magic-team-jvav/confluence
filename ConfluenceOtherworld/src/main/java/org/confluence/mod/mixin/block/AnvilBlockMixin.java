package org.confluence.mod.mixin.block;

import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.confluence.mod.util.ModUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin {
    @Shadow
    @Final
    public static DirectionProperty FACING;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private static void modify(BlockState state, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(ModUtils.getLeadAnvilDamage(state, FACING));
    }
}
