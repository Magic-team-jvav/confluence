package org.confluence.mod.common.attachment;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import PortLib.extensions.net.minecraft.core.HolderLookup.PortHolderLookupExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.mod.network.s2c.DropletsSyncPacketS2C;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;

import java.util.*;

public class ChunkDropletsData implements IPortNBTSerializable<CompoundTag> {
    private final Map<ChunkPos, Map<BlockPos, ParticleOptions>> dataMap = new Hashtable<>();
    private final transient Map<UUID, Set<ChunkPos>> lastSync = new HashMap<>();

    public Map<ChunkPos, Map<BlockPos, ParticleOptions>> getDataMap() {
        return dataMap;
    }

    public Map<ChunkPos, Map<BlockPos, ParticleOptions>> getDataMap(Player player, boolean force) {
        Map<ChunkPos, Map<BlockPos, ParticleOptions>> map = null;
        ChunkPos center = null;
        Set<ChunkPos> posSet = null;
        for (int i = -1; i <= 1; i++) {
            boolean a = i == 0;
            for (int j = -1; j <= 1; j++) {
                if (map == null) {
                    map = new HashMap<>();
                    center = player.chunkPosition();
                    posSet = lastSync.computeIfAbsent(player.getUUID(), uuid -> new HashSet<>());
                }
                ChunkPos pos = a && j == 0 ? center : new ChunkPos(center.x + i, center.z + j);
                Map<BlockPos, ParticleOptions> map1 = dataMap.get(pos);
                if (map1 == null) continue;
                if (!force && posSet.contains(pos)) continue;
                map.put(pos, map1);
                posSet.add(pos);
            }
        }
        return map;
    }

    public Map<UUID, Set<ChunkPos>> getLastSync() {
        return lastSync;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();

        ListTag listTag = new ListTag();
        RegistryOps<Tag> ops = PortHolderLookupExtension.Provider.createSerializationContext(provider, NbtOps.INSTANCE);
        for (Map.Entry<ChunkPos, Map<BlockPos, ParticleOptions>> entry : dataMap.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            CompoundTag map = new CompoundTag();
            map.putInt("x", chunkPos.x);
            map.putInt("z", chunkPos.z);
            ListTag data = new ListTag();
            for (Map.Entry<BlockPos, ParticleOptions> posEntry : entry.getValue().entrySet()) {
                PortDataResultExtension.ifSuccess(ParticleTypes.CODEC.encodeStart(ops, posEntry.getValue()), result -> {
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("pos", LibUtils.compressRelativePos(posEntry.getKey()));
                    tag.put("particle", result);
                    data.add(tag);
                });
            }
            map.put("data", data);
            listTag.add(map);
        }
        nbt.put("dataMap", listTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        dataMap.clear();
        ListTag listTag = nbt.getList("dataMap", Tag.TAG_COMPOUND);
        RegistryOps<Tag> ops = PortHolderLookupExtension.Provider.createSerializationContext(provider, NbtOps.INSTANCE);
        for (Tag tag : listTag) {
            CompoundTag map = (CompoundTag) tag;
            ChunkPos chunkPos = new ChunkPos(map.getInt("x"), map.getInt("z"));
            Map<BlockPos, ParticleOptions> value = new Hashtable<>();
            for (Tag data : map.getList("data", Tag.TAG_COMPOUND)) {
                CompoundTag compoundTag = (CompoundTag) data;
                PortDataResultExtension.ifSuccess(ParticleTypes.CODEC.parse(ops, compoundTag.get("particle")), result -> value.put(
                        LibUtils.decompressRelativePos(chunkPos, compoundTag.getInt("pos")), result
                ));
            }
            dataMap.put(chunkPos, value);
        }
    }

    public static void syncDroplets(ServerPlayer player) {
        if (IServerPlayer.of(player).confluence$chunkPosChanged()) {
            ChunkDropletsData data = of(player.serverLevel());
            Map<ChunkPos, Map<BlockPos, ParticleOptions>> dataMap = data.getDataMap(player, false);
            if (!dataMap.isEmpty() || data.getLastSync().computeIfAbsent(player.getUUID(), uuid -> new HashSet<>()).stream().anyMatch(dataMap.keySet()::contains)) {
                DropletsSyncPacketS2C.sendToClient(player, dataMap);
            }
        }
    }

    public static ChunkDropletsData of(Level level) {
        return level.getAttach(ModAttachmentTypes.CHUNK_DROPLETS_DATA);
    }
}
