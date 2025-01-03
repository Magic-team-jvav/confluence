package org.confluence.mod.network.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.textures.LocalBrushData;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;
import java.util.Map;

public record BrushingColorPacketS2C(BrushData data) implements CustomPacketPayload {
    public static final Type<BrushingColorPacketS2C> TYPE = new Type<>(Confluence.asResource("brushing_color"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BrushingColorPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull BrushingColorPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            return new BrushingColorPacketS2C(buffer.readJsonWithCodec(BrushData.CODEC));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, BrushingColorPacketS2C value) {
            buffer.writeJsonWithCodec(BrushData.CODEC, value.data);
        }
    };

    @Override
    public @NotNull Type<BrushingColorPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                LocalBrushData.handlePacket(this);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void sendToClient(ServerPlayer serverPlayer, ChunkPos chunkPos, BrushData data, boolean save) {
        if (save) saveData(serverPlayer.serverLevel(), chunkPos, data);
        PacketDistributor.sendToPlayer(serverPlayer, new BrushingColorPacketS2C(data));
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, BrushData data, boolean save) {
        if (save) saveData(level, chunkPos, data);
        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, new BrushingColorPacketS2C(data));
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, BlockPos pos, BrushData.Facing facing, int color, boolean save) {
        sendToPlayersTrackingChunk(level, new ChunkPos(pos), new BrushData(pos, facing, color), save);
    }

    public static void remove(ServerLevel level, BlockPos pos, BrushData.Facing facing) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            BrushData brushData = level.getData(ModAttachmentTypes.CHUNK_BRUSH_DATA).getDataMap().get(new ChunkPos(pos));
            if (brushData != null) {
                BrushData.Entry entry = brushData.colors().get(pos);
                if (entry != null) {
                    entry.map().remove(facing);
                    PacketDistributor.sendToAllPlayers(new BrushingColorPacketS2C(new BrushData(pos, facing, -1)));
                }
            }
        }
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            BrushData brushData = level.getData(ModAttachmentTypes.CHUNK_BRUSH_DATA).getDataMap().get(new ChunkPos(pos));
            if (brushData != null) {
                brushData.removeEntry(pos);
                PacketDistributor.sendToAllPlayers(new BrushingColorPacketS2C(new BrushData(Map.of(pos, BrushData.EMPTY_ENTRY))));
            }
        }
    }

    private static void saveData(ServerLevel level, ChunkPos chunkPos, BrushData data) {
        level.getData(ModAttachmentTypes.CHUNK_BRUSH_DATA).getDataMap()
                .computeIfAbsent(chunkPos, pos -> new BrushData(new Hashtable<>()))
                .mergeData(data);
    }
}
