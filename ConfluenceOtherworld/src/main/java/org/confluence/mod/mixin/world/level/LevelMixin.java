package org.confluence.mod.mixin.world.level;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public abstract class LevelMixin {
    @ModifyReturnValue(method = "getBlockState", at = @At(value = "RETURN", ordinal = 1))
    private BlockState wrap(BlockState original) {
        return GlobalCloakData.INSTANCE.getTarget(original);
    }
}
