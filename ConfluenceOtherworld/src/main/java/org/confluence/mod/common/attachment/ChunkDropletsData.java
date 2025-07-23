package org.confluence.mod.common.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.lib.util.LibUtils;

import java.util.*;

public class ChunkDropletsData implements INBTSerializable<CompoundTag> {
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
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        for (Map.Entry<ChunkPos, Map<BlockPos, ParticleOptions>> entry : dataMap.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            CompoundTag map = new CompoundTag();
            map.putInt("x", chunkPos.x);
            map.putInt("z", chunkPos.z);
            ListTag data = new ListTag();
            for (Map.Entry<BlockPos, ParticleOptions> posEntry : entry.getValue().entrySet()) {
                ParticleTypes.CODEC.encodeStart(ops, posEntry.getValue()).ifSuccess(result -> {
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
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        for (Tag tag : listTag) {
            CompoundTag map = (CompoundTag) tag;
            ChunkPos chunkPos = new ChunkPos(map.getInt("x"), map.getInt("z"));
            Map<BlockPos, ParticleOptions> value = new Hashtable<>();
            for (Tag data : map.getList("data", Tag.TAG_COMPOUND)) {
                CompoundTag compoundTag = (CompoundTag) data;
                ParticleTypes.CODEC.parse(ops, compoundTag.get("particle")).ifSuccess(result -> value.put(
                        LibUtils.decompressRelativePos(chunkPos, compoundTag.getInt("pos")), result
                ));
            }
            dataMap.put(chunkPos, value);
        }
    }
}
