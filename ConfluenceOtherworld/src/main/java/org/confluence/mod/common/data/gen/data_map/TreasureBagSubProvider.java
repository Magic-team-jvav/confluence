package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.TreasureBagDrop;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.item.TreasureBagItems;
import org.mesdag.portlib.datamap.PortDataMapProvider;

public final class TreasureBagSubProvider {
    public static void gather(ModDataMapProvider.Appender<PortDataMapProvider.Builder<TreasureBagDrop, EntityType<?>>> appender) {
        appender.create()
                .add(BossEntities.KING_SLIME, new TreasureBagDrop(TreasureBagItems.KING_SLIME_TREASURE_BAG.get()), false)
                .add(BossEntities.EYE_OF_CTHULHU, new TreasureBagDrop(TreasureBagItems.EYE_OF_CTHULHU_TREASURE_BAG.get()), false)
                .add(BossEntities.EATER_OF_WORLDS, new TreasureBagDrop(TreasureBagItems.EATER_OF_WORLDS_TREASURE_BAG.get()), false)
                .add(BossEntities.BRAIN_OF_CTHULHU, new TreasureBagDrop(TreasureBagItems.BRAIN_OF_CTHULHU_TREASURE_BAG.get()), false)
                .add(BossEntities.QUEEN_BEE, new TreasureBagDrop(TreasureBagItems.QUEEN_BEE_TREASURE_BAG.get()), false)
                .add(BossEntities.DEERCLOPS, new TreasureBagDrop(TreasureBagItems.DEERCLOPS_TREASURE_BAG.get()), false)
                .add(BossEntities.SKELETRON, new TreasureBagDrop(TreasureBagItems.SKELETRON_TREASURE_BAG.get()), false)
                .add(BossEntities.WALL_OF_FLESH, new TreasureBagDrop(TreasureBagItems.WALL_OF_FLESH_TREASURE_BAG.get()), false)
                .add(BossEntities.HILL_OF_FLESH, new TreasureBagDrop(TreasureBagItems.HILL_OF_FLESH_TREASURE_BAG.get()), false)
                .add(BossEntities.THE_TWINS, new TreasureBagDrop(TreasureBagItems.THE_TWINS_TREASURE_BAG.get()), false)
                .add(BossEntities.SKELETRON_PRIME, new TreasureBagDrop(TreasureBagItems.SKELETRON_PRIME_TREASURE_BAG.get()), false)
                .add(BossEntities.THE_DESTROYER, new TreasureBagDrop(TreasureBagItems.THE_DESTROYER_TREASURE_BAG.get()), false)
                .add(BossEntities.PLANTERA, new TreasureBagDrop(TreasureBagItems.PLANTERA_TREASURE_BAG.get()), false)
                .add(BossEntities.LUNATIC_CULTIST, new TreasureBagDrop(TreasureBagItems.LUNATIC_CULTIST_TREASURE_BAG.get()), false);
    }
}
