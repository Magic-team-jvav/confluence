package org.confluence.mod.mixin.integration.terraentity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.SpawnPlacements;
import org.confluence.mod.integration.terra_entity.TEHelper;
import org.confluence.terraentity.entity.animal.SimpleVariantAnimal;
import org.confluence.terraentity.init.entity.TEAnimals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TEAnimals.class, remap = false)
public abstract class TEAnimalsMixin {
    @ModifyReturnValue(method = "wormSpawnRules", at = @At("RETURN"))
    private static SpawnPlacements.SpawnPredicate<SimpleVariantAnimal> modify(SpawnPlacements.SpawnPredicate<SimpleVariantAnimal> original) {
        return TEHelper::wormSpawnRules;
    }
}
