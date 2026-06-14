package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.item.Item;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.DiggingPower;

public final class DiggingPowerProvider {
    public static void gather(ModDataMapProvider.Appender<DataMapProvider.Builder<DiggingPower, Item>> appender) {
        appender.create();
    }
}
