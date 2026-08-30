package org.confluence.mod.mixin.integration.ars_nouveau;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.common.capability.ManaCap", remap = false)
public abstract class ManaCapMixin {
    @Shadow
    LivingEntity entity;

    @Unique
    private Player confluence$player;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(LivingEntity livingEntity, CallbackInfo ci) {
        if (CommonConfigs.CONVERT_ARS_NOUVEAU_MANA.get() && livingEntity instanceof Player player) {
            this.confluence$player = player;
        }
    }

    @Inject(method = "setMaxMana", at = @At("TAIL"))
    private void updateMana(CallbackInfo ci) {
        ArsNouveauHelper.updateMana(entity);
    }

    @Inject(method = "removeMana", at = @At("RETURN"))
    private void removeData(double manaToRemove, CallbackInfoReturnable<Double> cir) {
        if (confluence$player instanceof ServerPlayer player) {
            ArsNouveauHelper.extractMana(player, manaToRemove);
        }
    }
}
