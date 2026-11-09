package org.confluence.mod.mixin.integration.flowingfluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.integration.flowingfluids.FlowingFluidsBridge;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/// Flowing Fluids 的侧向流动（ff$flowToSides，由它的 MixinFlowingFluid 加进 FlowingFluid）：平地上改成向所有更低的邻格均匀摊开，
/// 见 FlowingFluidsBridge.spreadEvenly。优先级高于 Flowing Fluids（默认 1000），保证它的方法已经合并进来；
/// 没装 Flowing Fluids 时这个方法不存在，require = 0 直接跳过。
@Mixin(value = FlowingFluid.class, priority = 1500)
public abstract class FlowingFluidEvenSpreadMixin {
    @Dynamic("Added to FlowingFluid by Flowing Fluids (traben.flowing_fluids.mixin.mixins.MixinFlowingFluid)")
    @Inject(method = "ff$flowToSides", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private void confluence$spreadEvenly(Level level, BlockPos pos, FluidState state, int amount, BlockState blockState,
                                         CallbackInfoReturnable<Direction> cir) {
        Direction result = FlowingFluidsBridge.spreadEvenly((FlowingFluid) (Object) this, level, pos, state, amount, blockState);
        if (result != FlowingFluidsBridge.DEFER) cir.setReturnValue(result);
    }
}
