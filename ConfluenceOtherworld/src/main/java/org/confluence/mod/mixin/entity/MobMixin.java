package org.confluence.mod.mixin.entity;

import net.minecraft.world.entity.Mob;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin implements SelfGetter<Mob> {
    @Inject(method = "isSunBurnTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getLightLevelDependentMagicValue()F"), cancellable = true)
    private void checkGraveyard(CallbackInfoReturnable<Boolean> cir) {
        IChunkSection iSection = DynamicBiomeUtils.getISection(confluence$self().level(), confluence$self().blockPosition());
        if (iSection != null && iSection.confluence$isGraveyard()) {
            cir.setReturnValue(false);
        }
    }
}
