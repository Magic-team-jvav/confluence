package org.confluence.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.integration.terra_entity.TERemoval;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ServerLifecycleHooks.class)
public class ServerLifecycleHooksMixin {
    @ModifyExpressionValue(method = "runModifiers", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;", ordinal = 0))
    private static List<BiomeModifier> modifyStreamToList(List<BiomeModifier> original, @Local RegistryAccess registries) {
        return TERemoval.processSpawners(original, registries.registryOrThrow(NeoForgeRegistries.Keys.BIOME_MODIFIERS));
    }
}
