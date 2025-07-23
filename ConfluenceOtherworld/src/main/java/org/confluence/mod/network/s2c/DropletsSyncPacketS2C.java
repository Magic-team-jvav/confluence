package org.confluence.mod.network.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.DropletsHandler;

import java.util.HashMap;
import java.util.Map;

public record DropletsSyncPacketS2C(Map<ChunkPos, Map<BlockPos, ParticleOptions>> data) implements CustomPacketPayload {
    public static final Type<DropletsSyncPacketS2C> TYPE = new Type<>(Confluence.asResource("droplets_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DropletsSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DropletsSyncPacketS2C decode(RegistryFriendlyByteBuf buffer) {
            int amount = buffer.readVarInt();
            Map<ChunkPos, Map<BlockPos, ParticleOptions>> mapMap = new HashMap<>();
            for (int i = 0; i < amount; i++) {
                ChunkPos pos = buffer.readChunkPos();
                int size = buffer.readVarInt();
                Map<BlockPos, ParticleOptions> map = new HashMap<>();
                for (int j = 0; j < size; j++) {
                    BlockPos blockPos = LibUtils.decompressRelativePos(pos, buffer.readVarInt());
                    ParticleOptions particle = ParticleTypes.STREAM_CODEC.decode(buffer);
                    map.put(blockPos, particle);
                }
                mapMap.put(pos, map);
            }
            return new DropletsSyncPacketS2C(mapMap);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, DropletsSyncPacketS2C value) {
            buffer.writeVarInt(value.data.size());
            for (Map.Entry<ChunkPos, Map<BlockPos, ParticleOptions>> entry : value.data.entrySet()) {
                buffer.writeChunkPos(entry.getKey());
                buffer.writeVarInt(entry.getValue().size());
                for (Map.Entry<BlockPos, ParticleOptions> entry1 : entry.getValue().entrySet()) {
                    buffer.writeVarInt(LibUtils.compressRelativePos(entry1.getKey()));
                    ParticleTypes.STREAM_CODEC.encode(buffer, entry1.getValue());
                }
            }
        }
    };

    @Override
    public Type<DropletsSyncPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                DropletsHandler.handlePacket(this);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }
}
