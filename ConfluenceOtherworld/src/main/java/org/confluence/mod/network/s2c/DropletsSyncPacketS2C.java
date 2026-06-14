package org.confluence.mod.network.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.DropletsHandler;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.HashMap;
import java.util.Map;

public record DropletsSyncPacketS2C(
        Map<ChunkPos, Map<BlockPos, ParticleOptions>> data) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("droplets_sync");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, DropletsSyncPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DropletsSyncPacketS2C decode(PortRegistryFriendlyByteBuf buffer) {
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
        public void encode(PortRegistryFriendlyByteBuf buffer, DropletsSyncPacketS2C value) {
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
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        DropletsHandler.handlePacket(this);
    }

    public static void sendToClient(ServerPlayer player, Map<ChunkPos, Map<BlockPos, ParticleOptions>> dataMap) {
        Confluence.NETWORK_HANDLER.sendToPlayer(player, new DropletsSyncPacketS2C(dataMap));
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, Map<ChunkPos, Map<BlockPos, ParticleOptions>> dataMap) {
        Confluence.NETWORK_HANDLER.sendToPlayersTrackingChunk(level, chunkPos, new DropletsSyncPacketS2C(dataMap));
    }
}
