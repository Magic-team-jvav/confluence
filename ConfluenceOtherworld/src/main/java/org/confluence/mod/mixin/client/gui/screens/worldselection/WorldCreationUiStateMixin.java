package org.confluence.mod.mixin.client.gui.screens.worldselection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.mixed.IWorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.OptionalLong;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {
    @Shadow
    public abstract String getSeed();

    @WrapOperation(method = "lambda$setSeed$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/WorldOptions;withSeed(Ljava/util/OptionalLong;)Lnet/minecraft/world/level/levelgen/WorldOptions;"))
    private WorldOptions checkSecretSeed(WorldOptions instance, OptionalLong seed, Operation<WorldOptions> original) {
        instance = IWorldOptions.of(instance).confluence$copyWithoutSecretFlag();
        if (!getSeed().isEmpty()) {
            ObjectBooleanPair<WorldOptions> pair = ModSecretSeeds.matchSeed(getSeed(), instance);
            if (pair.rightBoolean()) {
                return pair.left();
            }
        }
        return original.call(instance, seed);
    }
}
