package org.confluence.mod.common.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.BrushData;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Hashtable;
import java.util.Map;

public class ChunkBrushData implements INBTSerializable<CompoundTag> {
    private final Map<ChunkPos, BrushData> dataMap = new Hashtable<>();

    public Map<ChunkPos, BrushData> getDataMap() {
        return dataMap;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        ListTag listTag = new ListTag();
        for (Map.Entry<ChunkPos, BrushData> entry : dataMap.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            CompoundTag map = new CompoundTag();
            map.putInt("x", chunkPos.x);
            map.putInt("z", chunkPos.z);
            ListTag data = new ListTag();
            for (Map.Entry<BlockPos, int[]> posEntry : entry.getValue().colors().entrySet()) {
                int[] array = new int[7];
                array[0] = LibUtils.compressRelativePos(posEntry.getKey());
                System.arraycopy(posEntry.getValue(), 0, array, 1, 6);
                data.add(new IntArrayTag(array));
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
        for (Tag tag : listTag) {
            CompoundTag map = (CompoundTag) tag;
            ChunkPos chunkPos = new ChunkPos(map.getInt("x"), map.getInt("z"));
            Map<BlockPos, int[]> value = new Hashtable<>();
            for (Tag data : map.getList("data", Tag.TAG_INT_ARRAY)) {
                int[] array = ((IntArrayTag) data).getAsIntArray();
                int[] colors = new int[6];
                System.arraycopy(array, 1, colors, 0, 6);
                value.put(LibUtils.decompressRelativePos(chunkPos, array[0]), colors);
            }
            dataMap.put(chunkPos, new BrushData(value));
        }
    }

    public static ChunkBrushData of(Level level) {
        return level.getData(ModAttachmentTypes.CHUNK_BRUSH_DATA);
    }
}
