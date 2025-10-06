package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ToolItems;

public final class FurnaceFuelSubProvider {
    public static void gather(ModDataMapProvider.Appender<DataMapProvider.Builder<FurnaceFuel, Item>> appender) {
        appender.create()
                .add(MaterialItems.GEL.getKey(), new FurnaceFuel(160), false)
                .add(MaterialItems.PINK_GEL.getKey(), new FurnaceFuel(320), false)
                .add(ToolItems.BOTTOMLESS_LAVA_BUCKET.getKey(), new FurnaceFuel(0x3F3F3F3F), false)
                .add(ArmorItems.PLANK_HELMET.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.PLANK_CHESTPLATE.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.PLANK_LEGGINGS.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.PLANK_BOOTS.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.EBONY_HELMET.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.EBONY_CHESTPLATE.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.EBONY_LEGGINGS.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.EBONY_BOOTS.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.SHADOW_PLANK_HELMET.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.SHADOW_PLANK_CHESTPLATE.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.SHADOW_PLANK_LEGGINGS.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.SHADOW_PLANK_BOOTS.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.PEARL_HELMET.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.PEARL_CHESTPLATE.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.PEARL_LEGGINGS.getKey(), new FurnaceFuel(300), false)
                .add(ArmorItems.PEARL_BOOTS.getKey(), new FurnaceFuel(300), false);
    }
}
