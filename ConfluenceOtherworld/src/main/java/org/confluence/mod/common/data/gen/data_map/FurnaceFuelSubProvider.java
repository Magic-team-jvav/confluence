package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.item.Item;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.init.item.ArmorItems;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.mesdag.portlib.datamap.PortDataMapProvider;
import org.mesdag.portlib.datamap.builtin.PortFurnaceFuel;

public final class FurnaceFuelSubProvider {
    public static void gather(ModDataMapProvider.Appender<PortDataMapProvider.Builder<PortFurnaceFuel, Item>> appender) {
        appender.create()
                .add(MaterialItems.GEL.getKey(), new PortFurnaceFuel(160), false)
                .add(MaterialItems.PINK_GEL.getKey(), new PortFurnaceFuel(320), false)
                .add(ToolItems.BOTTOMLESS_LAVA_BUCKET.getKey(), new PortFurnaceFuel(0x3F3F3F3F), false)
                .add(ArmorItems.PLANK_HELMET.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.PLANK_CHESTPLATE.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.PLANK_LEGGINGS.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.PLANK_BOOTS.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.EBONY_HELMET.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.EBONY_CHESTPLATE.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.EBONY_LEGGINGS.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.EBONY_BOOTS.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.SHADOW_PLANK_HELMET.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.SHADOW_PLANK_CHESTPLATE.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.SHADOW_PLANK_LEGGINGS.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.SHADOW_PLANK_BOOTS.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.PEARL_HELMET.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.PEARL_CHESTPLATE.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.PEARL_LEGGINGS.getKey(), new PortFurnaceFuel(300), false)
                .add(ArmorItems.PEARL_BOOTS.getKey(), new PortFurnaceFuel(300), false);
    }
}
