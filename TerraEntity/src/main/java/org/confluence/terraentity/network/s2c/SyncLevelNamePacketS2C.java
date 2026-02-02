package org.confluence.terraentity.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.utils.AdapterUtils;

public record SyncLevelNamePacketS2C(String name) implements CustomPacketPayload {
    public static final Type<SyncLevelNamePacketS2C> TYPE = new Type<>(TerraEntity.space("sync_level_name"));
    public static final StreamCodec<ByteBuf, SyncLevelNamePacketS2C> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(SyncLevelNamePacketS2C::new, SyncLevelNamePacketS2C::name);

    public static String levelName = "Otherworld";

    @Override
    public Type<SyncLevelNamePacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> levelName = name).exceptionally(e -> null);
    }

    public static void sendToClient(ServerPlayer player) {
        AdapterUtils.sendToPlayer(player, new SyncLevelNamePacketS2C(player.server.getWorldData().getLevelName()));
    }
}
