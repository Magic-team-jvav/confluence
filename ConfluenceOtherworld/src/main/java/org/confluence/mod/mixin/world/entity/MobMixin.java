package org.confluence.mod.mixin.world.entity;

import net.minecraft.world.entity.Mob;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.mixed.ILevelChunkSection;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.init.ModBlockCounters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin implements SelfGetter<Mob> {
    @Inject(method = "isSunBurnTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getLightLevelDependentMagicValue()F"), cancellable = true)
    private void checkGraveyard(CallbackInfoReturnable<Boolean> cir) {
        ILevelChunkSection iSection = DynamicBiomeUtils.getISection(confluence$self().level(), confluence$self().blockPosition());
        if (ModBlockCounters.isGraveyard(iSection)) {
            cir.setReturnValue(false);
        }
    }
}
