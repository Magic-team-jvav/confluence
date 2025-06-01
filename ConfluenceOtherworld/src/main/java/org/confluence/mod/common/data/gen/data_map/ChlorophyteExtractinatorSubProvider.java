package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.ExtractinatorData;

public class ChlorophyteExtractinatorSubProvider {
    public static void gather(ModDataMapProvider.Appender<DataMapProvider.Builder<ExtractinatorData, Item>> appender) {
        appender.create();
    }
}
