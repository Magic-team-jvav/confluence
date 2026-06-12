package org.confluence.mod.network.c2s;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.Confluence;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.util.ModUtils;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record SwitchEffectEnabledPackedC2S(Holder<MobEffect> effect,
                                           boolean enabled) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("switch_effect_enabled");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, SwitchEffectEnabledPackedC2S> STREAM_CODEC = PortStreamCodec.composite(
            MobEffect.STREAM_CODEC, SwitchEffectEnabledPackedC2S::effect,
            PortByteBufCodecs.BOOL, SwitchEffectEnabledPackedC2S::enabled,
            SwitchEffectEnabledPackedC2S::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        MobEffectInstance instance = player.getActiveEffectsMap().get(effect);
        if (instance != null && ModUtils.isSwitchableEffect(instance)) {
            IMobEffectInstance.of(instance).confluence$setEnabled(enabled);
            MobEffect mobEffect = instance.getEffect();
            if (enabled) {
                mobEffect.addAttributeModifiers(player, player.getAttributes(), instance.getAmplifier());
            } else {
                mobEffect.removeAttributeModifiers(player, player.getAttributes(), instance.getAmplifier());
            }
            player.effectsDirty = true;
        }
    }

    public static void sendToServer(Holder<MobEffect> effect, boolean enabled) {
        Confluence.NETWORK_HANDLER.sendToServer(new SwitchEffectEnabledPackedC2S(effect, enabled));
    }
}
