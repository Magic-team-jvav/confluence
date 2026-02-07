package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.ImmunityDataMap;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.confluence.terra_curio.common.init.TCEntities;

public final class ImmunitySubProvider {
    public static void gather(ModDataMapProvider.Appender<DataMapProvider.Builder<ImmunityDataMap, EntityType<?>>> appender) {
        appender.create()
                .add(ModEntities.VILETHRON_PROJECTILE, new ImmunityDataMap(Immunity.Type.STATIC, 5), false)
                .add(ModEntities.CRYSTAL_VILE_SHARD_PROJECTILE, new ImmunityDataMap(Immunity.Type.STATIC, 5), false)
                .add(TCEntities.BEE_PROJECTILE, new ImmunityDataMap(Immunity.Type.STATIC, 8), false)
                .add(ModEntities.BEE_GUN_BULLET, new ImmunityDataMap(Immunity.Type.STATIC, 8), false)
                .add(ModEntities.GOLDEN_SHOWER_PROJECTILE, new ImmunityDataMap(Immunity.Type.STATIC, 4), false)
                .add(ModEntities.WATER_STREAM_PROJECTILE, new ImmunityDataMap(Immunity.Type.STATIC, 4), false)
                .add(ModEntities.NIGHTS_EDGE_PROJECTILE, new ImmunityDataMap(Immunity.Type.STATIC, 20), false)
                .add(TCEntities.X_BONE, new ImmunityDataMap(Immunity.Type.STATIC, 4), false);
    }
}
