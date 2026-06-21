package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.ImmunityDataMap;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.confluence.terra_curio.common.init.TCEntities;
import org.mesdag.portlib.datamap.PortDataMapProvider;

public final class ImmunitySubProvider {
    public static void gather(ModDataMapProvider.Appender<PortDataMapProvider.Builder<ImmunityDataMap, EntityType<?>>> appender) {
        appender.create()
                .add(ModEntities.VILETHRON, new ImmunityDataMap(Immunity.Type.STATIC, 5), false)
                .add(ModEntities.CRYSTAL_VILE_SHARD, new ImmunityDataMap(Immunity.Type.STATIC, 5), false)
                .add(TCEntities.BEE_PROJECTILE, new ImmunityDataMap(Immunity.Type.STATIC, 8), false)
                .add(ModEntities.BEE_GUN_BULLET, new ImmunityDataMap(Immunity.Type.STATIC, 8), false)
                .add(ModEntities.GOLDEN_SHOWER, new ImmunityDataMap(Immunity.Type.STATIC, 4), false)
                .add(ModEntities.WATER_STREAM, new ImmunityDataMap(Immunity.Type.STATIC, 4), false)
                .add(ModEntities.NIGHTS_EDGE, new ImmunityDataMap(Immunity.Type.STATIC, 20), false)
                .add(TEProjectileEntities.ICE_PILLAR, new ImmunityDataMap(Immunity.Type.STATIC, 20), false)
                .add(TEProjectileEntities.SLIME_SPIKE, new ImmunityDataMap(Immunity.Type.STATIC, 5), false)
                .add(TEProjectileEntities.FIRE_IMP_PROJ, new ImmunityDataMap(Immunity.Type.LOCAL, 1), false)
                .add(TEProjectileEntities.SUMMON_BEE_STICK_PROJ, new ImmunityDataMap(Immunity.Type.LOCAL, 1), false)
                .add(ModEntities.MAGIC_MISSILE, new ImmunityDataMap(Immunity.Type.STATIC, 7), false)
                .add(ModEntities.RAIN, new ImmunityDataMap(Immunity.Type.LOCAL, 4), false)
                .add(TCEntities.X_BONE, new ImmunityDataMap(Immunity.Type.STATIC, 4), false);
    }
}
