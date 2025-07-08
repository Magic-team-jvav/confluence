package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.DataMapProvider;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.ImmunityDataMap;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.mixed.Immunity;
import org.confluence.terra_curio.common.init.TCEntities;

public class ImmunitySubProvider {
    public static void gather(ModDataMapProvider.Appender<DataMapProvider.Builder<ImmunityDataMap, EntityType<?>>> appender) {
        appender.create()
                .add(ModEntities.VILETHRON_PROJECTILE, new ImmunityDataMap(Immunity.Type.STATIC, 5), false)
                .add(TCEntities.BEE_PROJECTILE, new ImmunityDataMap(Immunity.Type.LOCAL, 8), false);
    }
}
