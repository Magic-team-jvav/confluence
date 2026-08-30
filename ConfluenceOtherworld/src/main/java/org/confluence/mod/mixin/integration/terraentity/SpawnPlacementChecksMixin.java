package org.confluence.mod.mixin.integration.terraentity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.terraentity.entity.util.SpawnPlacementChecks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SpawnPlacementChecks.class, remap = false)
public abstract class SpawnPlacementChecksMixin {
    @ModifyReturnValue(method = "checkHardmode", at = @At("RETURN"))
    private static <T extends Entity> SpawnPlacements.SpawnPredicate<T> wrap(SpawnPlacements.SpawnPredicate<T> original) {
        return (entityType, serverLevel, spawnType, pos, random) -> {
            boolean hardmode = IMinecraftServer.isHardmode(serverLevel.getLevel().getServer());
            return hardmode && original.test(entityType, serverLevel, spawnType, pos, random);
        };
    }
}
