package org.confluence.mod.mixin;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.mixed.IClientboundUpdateMobEffectPacket;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientboundUpdateMobEffectPacket.class, priority = 1145)
public abstract class ClientboundUpdateMobEffectPacketMixin implements IClientboundUpdateMobEffectPacket {
    @Unique
    private boolean confluence$enabled = true;

    @Override
    public boolean confluence$isEnabled() {
        return confluence$enabled;
    }

    @Inject(method = "<init>(ILnet/minecraft/world/effect/MobEffectInstance;Z)V", at = @At("TAIL"))
    private void init(int entityId, MobEffectInstance effect, boolean blend, CallbackInfo ci) {
        this.confluence$enabled = IMobEffectInstance.of(effect).confluence$isEnabled();
    }

    @Inject(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("TAIL"))
    private void init(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        this.confluence$enabled = buffer.readBoolean();
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void encode(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeBoolean(confluence$enabled);
    }
}
