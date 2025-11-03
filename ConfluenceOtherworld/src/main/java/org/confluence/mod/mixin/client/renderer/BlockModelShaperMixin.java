package org.confluence.mod.mixin.client.renderer;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BlockModelShaper.class, priority = 1100)
public abstract class BlockModelShaperMixin {
    @ModifyVariable(method = "getBlockModel", at = @At("HEAD"), argsOnly = true)
    private BlockState simulator(BlockState state) {
        return GlobalCloakData.INSTANCE.getTarget(state);
    }
}
