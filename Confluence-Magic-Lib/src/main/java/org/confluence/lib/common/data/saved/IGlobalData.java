package org.confluence.lib.common.data.saved;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/// 用于整个存档的数据，而非维度数据（SavedData）
public interface IGlobalData {
    default <T> void decode(Dynamic<T> tag) {
        decode(convert(tag));
    }

    default void decode(CompoundTag tag) {}

    default <T> CompoundTag convert(Dynamic<T> dynamic) {
        Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        return (CompoundTag) tag;
    }

    void encode(CompoundTag tag);

    String serializeKey();

    /// 退出存档时自动清空缓存
    default void clear() {}

    List<IGlobalData> DAT = new CopyOnWriteArrayList<>();

    /// 必须调用这个，不然无法调用序列化
    static void registerGlobalData(IGlobalData... data) {
        DAT.addAll(Arrays.asList(data));
    }

    static void clearAll() {
        for (IGlobalData data : DAT) {
            data.clear();
        }
    }
}
