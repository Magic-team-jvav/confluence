package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.potion.HealingPotionItem;
import org.confluence.mod.common.item.potion.ManaPotionItem;

public record KeyRequestPacketC2S(int key) implements CustomPacketPayload {
    public static final int KEY_HEALING = 0;
    public static final int KEY_MANA = 1;
    public static final int KEY_CLAIRVOYANCE = 2; // 水晶球给予的灵视
    public static final Type<KeyRequestPacketC2S> TYPE = new Type<>(Confluence.asResource("key_request"));
    public static final StreamCodec<ByteBuf, KeyRequestPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.key,
            KeyRequestPacketC2S::new
    );

    @Override
    public Type<KeyRequestPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                if (key == KEY_HEALING) {
                    HealingPotionItem.use(serverPlayer);
                } else if (key == KEY_MANA) {
                    ManaPotionItem.use(serverPlayer);
                } else if (key == KEY_CLAIRVOYANCE) {
                    serverPlayer.addEffect(new MobEffectInstance(ModEffects.CLAIRVOYANCE, MobEffectInstance.INFINITE_DURATION));
                    serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE).flushAbility(serverPlayer);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
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
