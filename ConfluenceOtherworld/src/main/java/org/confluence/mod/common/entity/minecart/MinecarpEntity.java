package org.confluence.mod.common.entity.minecart;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 这是鲤鱼矿车  结合minecart矿车和carp鲤鱼
 */
public class MinecarpEntity extends BaseMinecartEntity {
    public MinecarpEntity(EntityType<? extends BaseMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MinecarpEntity(Level level, double x, double y, double z, Abilities<? extends BaseMinecartEntity> abilities) {
        super(level, x, y, z, abilities);
    }

    // todo
}
