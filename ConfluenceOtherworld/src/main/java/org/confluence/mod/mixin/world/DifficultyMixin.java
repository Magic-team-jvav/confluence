package org.confluence.mod.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.ForTheWorthy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Difficulty.class)
public abstract class DifficultyMixin implements SelfGetter<Difficulty> {
    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    private Component ftw(Component original) {
        if (ModSecretSeeds.FOR_THE_WORTHY.match()) {
            return ForTheWorthy.getDifficultyName(confluence$self());
        }
        return original;
    }
}
