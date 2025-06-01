package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.confluence.mod.common.init.ModTags;

public class ExtractinatorSubProvider {
    public static void gather(ModDataMapProvider.Appender<DataMapProvider.Builder<ExtractinatorData, Item>> appender) {
        appender.create()
                .add(ModTags.Items.DESERT_FOSSIL, data("with_desert_fossil"), false)
                .add(Tags.Items.GRAVELS, data("with_gravel"), false)
                .add(ModTags.Items.JUNK, data("with_junk"), false)
                .add(ModTags.Items.SILT_BLOCK, data("with_silt_block"), false)
                .add(ModTags.Items.SLUSH, data("with_slush"), false)
                .add(ModTags.Items.MARINE_GRAVEL, data("with_marine_gravel"), false);
    }

    private static ExtractinatorData data(String path) {
        return new ExtractinatorData(ResourceKey.create(Registries.LOOT_TABLE, Confluence.asResource("gameplay/extract/" + path)));
    }
}
