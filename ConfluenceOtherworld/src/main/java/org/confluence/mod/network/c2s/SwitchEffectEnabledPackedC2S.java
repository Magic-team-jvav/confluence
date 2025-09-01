package org.confluence.mod.network.c2s;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.network.IPacket;
import org.confluence.mod.util.ModUtils;

public record SwitchEffectEnabledPackedC2S(Holder<MobEffect> effect, boolean enabled) implements IPacketC2S {
    public static final Type<SwitchEffectEnabledPackedC2S> TYPE = IPacket.createType("switch_effect_enabled");
    public static final StreamCodec<RegistryFriendlyByteBuf, SwitchEffectEnabledPackedC2S> STREAM_CODEC = StreamCodec.composite(
            MobEffect.STREAM_CODEC, SwitchEffectEnabledPackedC2S::effect,
            ByteBufCodecs.BOOL, SwitchEffectEnabledPackedC2S::enabled,
            SwitchEffectEnabledPackedC2S::new
    );

    @Override
    public Type<SwitchEffectEnabledPackedC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        MobEffectInstance instance = player.getActiveEffectsMap().get(effect);
        if (instance != null && ModUtils.isSwitchableEffect(instance)) {
            IMobEffectInstance.of(instance).confluence$setEnabled(enabled);
            MobEffect mobEffect = instance.getEffect().value();
            if (enabled) {
                mobEffect.addAttributeModifiers(player.getAttributes(), instance.getAmplifier());
            } else {
                mobEffect.removeAttributeModifiers(player.getAttributes());
            }
            player.effectsDirty = true;
        }
    }
}
