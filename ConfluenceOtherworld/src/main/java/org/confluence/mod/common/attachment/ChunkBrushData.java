package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.common.data.saved.BrushData;
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
            BrushData.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).ifSuccess(tag -> map.put("data", tag));
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
            BrushData.CODEC.parse(NbtOps.INSTANCE, map.get("data")).ifSuccess(brushData -> dataMap.put(chunkPos, brushData));
        }
    }
}
