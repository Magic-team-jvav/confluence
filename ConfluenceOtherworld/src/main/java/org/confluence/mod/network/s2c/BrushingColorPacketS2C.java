package org.confluence.mod.network.s2c;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.common.attachment.ChunkBrushData;
import org.confluence.mod.common.data.saved.BrushData;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Hashtable;
import java.util.Map;

public record BrushingColorPacketS2C(ChunkPos chunkPos, BrushData data) implements IPortPacket.S2C {
    public static final ResourceLocation ID = Confluence.asResource("brushing_color");
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, BrushingColorPacketS2C> STREAM_CODEC = new StreamCode<>() {
        @Override
        public BrushingColorPacketS2C decode(PortRegistryFriendlyByteBuf buffer) {
            ChunkPos chunkPos = buffer.readChunkPos();
            int size = buffer.readVarInt();
            Map<BlockPos, int[]> map = new Hashtable<>();
            for (int i = 0; i < size; i++) {
                BlockPos blockAt = LibUtils.decompressRelativePos(chunkPos, buffer.readInt());
                int[] list = map.computeIfAbsent(blockAt, BrushData.COMPUTE);
                byte face = buffer.readByte();
                for (int l = 0; l < 6; l++) {
                    int m = 1 << l;
                    if ((face & m) == m) {
                        list[LibUtils.DIRECTIONS[l].get3DDataValue()] = buffer.readInt();
                    }
                }
            }
            return new BrushingColorPacketS2C(chunkPos, new BrushData(map));
        }

        @Override
        public void encode(PortRegistryFriendlyByteBuf buffer, BrushingColorPacketS2C value) {
            buffer.writeChunkPos(value.chunkPos);
            Map<BlockPos, int[]> map = value.data.colors();
            buffer.writeVarInt(map.size());
            for (Map.Entry<BlockPos, int[]> entry : map.entrySet()) {
                buffer.writeInt(LibUtils.compressRelativePos(entry.getKey()));
                int[] color = entry.getValue();
                byte face = 0;
                IntArrayList list = new IntArrayList();
                for (int i = 0; i < 6; i++) {
                    int c = color[i];
                    if (c != BrushData.EMPTY_COLOR) {
                        face |= (byte) (1 << i);
                        list.add(c);
                    }
                }
                buffer.writeByte(face);
                for (int c : list) buffer.writeInt(c);
            }
        }
    };
    public static final int[] CLEAR_COLORS = BrushData.createColor(BrushData.CLEAR_COLOR);

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(Player player) {
        LocalBrushData.handlePacket(this);
    }

    public static void sendToClient(ServerPlayer serverPlayer, ChunkPos chunkPos, BrushData data, boolean save) {
        if (save) saveData(serverPlayer.serverLevel(), chunkPos, data);
        Confluence.NETWORK_HANDLER.sendToPlayer(serverPlayer, new BrushingColorPacketS2C(chunkPos, data));
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, BrushData data, boolean save) {
        if (save) saveData(level, chunkPos, data);
        Confluence.NETWORK_HANDLER.sendToPlayersTrackingChunk(level, chunkPos, new BrushingColorPacketS2C(chunkPos, data));
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, BlockPos pos, @Nullable Direction facing, int color, boolean save) {
        if (color == BrushData.ECHO_COLOR || !level.getBlockState(pos).isSolidRender(level, pos)) {
            facing = null;
        }
        sendToPlayersTrackingChunk(level, new ChunkPos(pos), new BrushData(pos, facing, color), save);
    }

    public static void remove(ServerLevel level, BlockPos pos, Direction facing) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            ChunkPos chunkPos = new ChunkPos(pos);
            BrushData brushData = ChunkBrushData.of(level).getDataMap().get(chunkPos);
            if (brushData == null) return;
            if (level.getBlockState(pos).isSolidRender(level, pos)) {
                brushData.remove(pos, facing);
            } else {
                brushData.remove(pos);
                facing = null;
            }
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new BrushingColorPacketS2C(chunkPos, new BrushData(pos, facing, BrushData.CLEAR_COLOR)));
        }
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        if (net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer() != null) {
            ChunkPos chunkPos = new ChunkPos(pos);
            BrushData brushData = ChunkBrushData.of(level).getDataMap().get(chunkPos);
            if (brushData == null) return;
            brushData.remove(pos);
            Confluence.NETWORK_HANDLER.sendToAllPlayers(new BrushingColorPacketS2C(chunkPos, new BrushData(Map.of(pos, CLEAR_COLORS))));
        }
    }

    private static void saveData(ServerLevel level, ChunkPos chunkPos, BrushData data) {
        ChunkBrushData.of(level).getDataMap()
                .computeIfAbsent(chunkPos, pos -> new BrushData(new Hashtable<>()))
                .merge(data);
    }
}
