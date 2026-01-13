package org.confluence.mod.common.data.saved;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import org.confluence.lib.common.data.saved.IGlobalData;

/// 事件系统重写到这
public class GameEventSystem implements IGlobalData {
    @Override
    public <T> void decode(Dynamic<T> tag) {

    }

    @Override
    public void encode(CompoundTag tag) {

    }

    @Override
    public String serializeKey() {
        return "confluence:game_event_system";
    }
}
