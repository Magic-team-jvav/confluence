package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.item.Item;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.mesdag.portlib.datamap.PortDataMapProvider;

public final class ChlorophyteExtractinatorSubProvider {
    public static void gather(ModDataMapProvider.Appender<PortDataMapProvider.Builder<ExtractinatorData, Item>> appender) {
        appender.create();
    }
}
