package org.confluence.mod.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.DifficultyInstance;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DifficultyInstance.class)
public abstract class DifficultyInstanceMixin {
    @ModifyReturnValue(method = "calculateDifficulty", at = @At("RETURN"))
    private float levelUp(float original) {
        if (ModSecretSeeds.FOR_THE_WORTHY.match()) {
            return original + 1;
        }
        return original;
    }
}
