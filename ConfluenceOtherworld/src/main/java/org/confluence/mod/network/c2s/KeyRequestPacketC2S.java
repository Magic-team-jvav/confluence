package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.potion.HealingPotionItem;
import org.confluence.mod.common.item.potion.ManaPotionItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record KeyRequestPacketC2S(byte key) implements IPortPacket.C2S {
    public static final byte KEY_HEALING = 0;
    public static final byte KEY_MANA = 1;
    public static final byte KEY_CLAIRVOYANCE = 2; // 水晶球给予的灵视
    public static final ResourceLocation ID = Confluence.asResource("key_request");
    public static final PortStreamCodec<ByteBuf, KeyRequestPacketC2S> STREAM_CODEC = PortByteBufCodecs.BYTE.map(KeyRequestPacketC2S::new, KeyRequestPacketC2S::key);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        if (key == KEY_HEALING) {
            HealingPotionItem.use(player);
        } else if (key == KEY_MANA) {
            ManaPotionItem.use(player);
        } else if (key == KEY_CLAIRVOYANCE) {
            player.addEffect(new MobEffectInstance(ModEffects.CLAIRVOYANCE, MobEffectInstance.INFINITE_DURATION));
            ManaStorage.of(player).flushAbility(player);
        }
    }

    public static void requestHealing() {
        Confluence.NETWORK_HANDLER.sendToServer(new KeyRequestPacketC2S(KEY_HEALING));
    }

    public static void requestMana() {
        Confluence.NETWORK_HANDLER.sendToServer(new KeyRequestPacketC2S(KEY_MANA));
    }

    public static void requestClairvoyance() {
        Confluence.NETWORK_HANDLER.sendToServer(new KeyRequestPacketC2S(KEY_CLAIRVOYANCE));
    }
}
