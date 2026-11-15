package org.confluence.mod.mixin.world.level;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin {
    @WrapMethod(method = "getBlockState")
    private BlockState wrap(BlockPos pos, Operation<BlockState> original) {
        return GlobalCloakData.INSTANCE.getTarget(original.call(pos));
    }
}
