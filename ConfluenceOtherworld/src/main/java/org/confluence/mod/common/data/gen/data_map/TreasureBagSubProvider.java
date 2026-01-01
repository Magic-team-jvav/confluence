package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.TreasureBagDrop;
import org.confluence.mod.common.init.item.TreasureBagItems;
import org.confluence.terraentity.init.entity.TEBossEntities;

public final class TreasureBagSubProvider {
    public static void gather(ModDataMapProvider.Appender<DataMapProvider.Builder<TreasureBagDrop, EntityType<?>>> appender) {
        appender.create()
                .add(TEBossEntities.KING_SLIME, new TreasureBagDrop(TreasureBagItems.KING_SLIME_TREASURE_BAG.get()), false)
                .add(TEBossEntities.EYE_OF_CTHULHU, new TreasureBagDrop(TreasureBagItems.EYE_OF_CTHULHU_TREASURE_BAG.get()), false)
                .add(TEBossEntities.EATER_OF_WORLDS, new TreasureBagDrop(TreasureBagItems.EATER_OF_WORLDS_TREASURE_BAG.get()), false)
                .add(TEBossEntities.BRAIN_OF_CTHULHU, new TreasureBagDrop(TreasureBagItems.BRAIN_OF_CTHULHU_TREASURE_BAG.get()), false)
                .add(TEBossEntities.QUEEN_BEE, new TreasureBagDrop(TreasureBagItems.QUEEN_BEE_TREASURE_BAG.get()), false)
                .add(TEBossEntities.DEERCLOPS, new TreasureBagDrop(TreasureBagItems.DEERCLOPS_TREASURE_BAG.get()), false)
                .add(TEBossEntities.SKELETRON, new TreasureBagDrop(TreasureBagItems.SKELETRON_TREASURE_BAG.get()), false)
                .add(TEBossEntities.DEERCLOPS, new TreasureBagDrop(TreasureBagItems.DEERCLOPS_TREASURE_BAG.get()), false)
                .add(TEBossEntities.WALL_OF_FLESH, new TreasureBagDrop(TreasureBagItems.WALL_OF_FLESH_TREASURE_BAG.get()), false)
                .add(TEBossEntities.HILL_OF_FLESH, new TreasureBagDrop(TreasureBagItems.HILL_OF_FLESH_TREASURE_BAG.get()), false);
    }
}
