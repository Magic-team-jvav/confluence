package org.confluence.mod.common.entity.minecart;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DemonicHellcartEntity extends BaseMinecartEntity {
    public DemonicHellcartEntity(EntityType<? extends BaseMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DemonicHellcartEntity(Level level, double x, double y, double z, Abilities<? extends BaseMinecartEntity> abilities) {
        super(level, x, y, z, abilities);
    }

    // todo
}
