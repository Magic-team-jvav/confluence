package org.confluence.mod.common.data.saved;

import net.minecraft.nbt.CompoundTag;
import org.confluence.lib.common.data.saved.IGlobalData;

/// 事件系统重写到这
public class GameEventSystem implements IGlobalData {
    @Override
    public void decode(CompoundTag tag) {

    }

    @Override
    public void encode(CompoundTag tag) {

    }

    @Override
    public String serializeKey() {
        return "confluence:game_event_system";
    }
}
