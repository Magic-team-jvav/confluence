package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.terra_curio.util.CuriosUtils;
import org.jetbrains.annotations.NotNull;

public record TheConstantPostEffectPacketS2C(boolean post) implements CustomPacketPayload {
    public static final Type<TheConstantPostEffectPacketS2C> TYPE = new Type<>(Confluence.asResource("the_constant_post_effect"));
    public static final StreamCodec<ByteBuf, TheConstantPostEffectPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, p -> p.post,
            TheConstantPostEffectPacketS2C::new
    );

    @Override
    public @NotNull Type<TheConstantPostEffectPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                TheConstant.postEffect(post);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        boolean secretSeed = ModSecretSeeds.THE_CONSTANT.match(serverPlayer.server);
        boolean accessory = CuriosUtils.hasCurio(serverPlayer, AccessoryItems.RADIO_THING.get());
        PacketDistributor.sendToPlayer(serverPlayer, new TheConstantPostEffectPacketS2C(secretSeed ^ accessory));
    }
}
