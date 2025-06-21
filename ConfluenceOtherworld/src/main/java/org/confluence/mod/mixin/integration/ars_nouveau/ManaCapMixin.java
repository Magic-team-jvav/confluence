package org.confluence.mod.mixin.integration.ars_nouveau;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.common.capability.ManaCap", remap = false)
public abstract class ManaCapMixin {
    @Shadow
    LivingEntity entity;

    @Shadow
    public abstract int getMaxMana();

    @Shadow
    public abstract double getCurrentMana();

    @Unique
    private Optional<ManaStorage> confluence$manaStorage = Optional.empty();
    @Unique
    private boolean confluence$isServerSide = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(LivingEntity livingEntity, CallbackInfo ci) {
        if (CommonConfigs.ARS_NOUVEAU_COMPATIBILITY.get() && livingEntity instanceof Player player) {
            this.confluence$manaStorage = Optional.of(player.getData(ModAttachmentTypes.MANA_STORAGE));
            this.confluence$isServerSide = player instanceof ServerPlayer;
        }
    }

    @ModifyReturnValue(method = "getCurrentMana", at = @At("RETURN"))
    private double getCurrentMana(double original) {
        if (confluence$isServerSide) {
            return confluence$manaStorage.map(storage -> ArsNouveauHelper.getCurrentMana(original, storage)).orElse(original);
        }
        return original;
    }

    @ModifyReturnValue(method = "getMaxMana", at = @At("RETURN"))
    private int getMaxMana(int original) {
        if (confluence$isServerSide) {
            return confluence$manaStorage.map(storage -> ArsNouveauHelper.getMaxMana(original, storage)).orElse(original);
        }
        return original;
    }

    @Inject(method = "removeMana", at = @At("RETURN"))
    private void removeData(double manaToRemove, CallbackInfoReturnable<Double> cir) {
        if (confluence$isServerSide) {
            confluence$manaStorage.ifPresent(storage -> ArsNouveauHelper.extractMana((ServerPlayer) entity, storage, manaToRemove));
        }
    }

    @Inject(method = "syncToClient", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/common/network/Networking;sendToPlayerClient(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void syncToClient(ServerPlayer player, CallbackInfo ci, @Local CompoundTag tag) {
        if (confluence$isServerSide) {
            tag.putDouble("current", getCurrentMana());
            tag.putInt("max", getMaxMana());
        }
    }
}
