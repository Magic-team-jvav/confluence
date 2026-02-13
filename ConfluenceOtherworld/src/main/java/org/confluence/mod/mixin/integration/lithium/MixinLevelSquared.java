package org.confluence.mod.mixin.integration.lithium;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = Level.class, priority = 1100)
public class MixinLevelSquared {
    @Dynamic
    @TargetHandler(mixin = "net.caffeinemc.mods.lithium.mixin.world.inline_block_access.LevelMixin", name = "getBlockState")
    @ModifyReturnValue(method = "@MixinSquared:Handler", at = @At(value = "RETURN", ordinal = 2))
    private BlockState wrap(BlockState original) {
        return GlobalCloakData.INSTANCE.getTarget(original);
    }
}
