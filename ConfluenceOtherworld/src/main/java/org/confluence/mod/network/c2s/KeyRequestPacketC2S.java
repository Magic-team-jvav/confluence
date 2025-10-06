package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.potion.HealingPotionItem;
import org.confluence.mod.common.item.potion.ManaPotionItem;
import org.confluence.mod.network.IPacket;

public record KeyRequestPacketC2S(byte key) implements IPacketC2S {
    public static final byte KEY_HEALING = 0;
    public static final byte KEY_MANA = 1;
    public static final byte KEY_CLAIRVOYANCE = 2; // 水晶球给予的灵视
    public static final Type<KeyRequestPacketC2S> TYPE = IPacket.createType("key_request");
    public static final StreamCodec<ByteBuf, KeyRequestPacketC2S> STREAM_CODEC = ByteBufCodecs.BYTE.map(KeyRequestPacketC2S::new, KeyRequestPacketC2S::key);

    @Override
    public Type<KeyRequestPacketC2S> type() {
        return TYPE;
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
        PacketDistributor.sendToServer(new KeyRequestPacketC2S(KEY_HEALING));
    }

    public static void requestMana() {
        PacketDistributor.sendToServer(new KeyRequestPacketC2S(KEY_MANA));
    }

    public static void requestClairvoyance() {
        PacketDistributor.sendToServer(new KeyRequestPacketC2S(KEY_CLAIRVOYANCE));
    }
}
