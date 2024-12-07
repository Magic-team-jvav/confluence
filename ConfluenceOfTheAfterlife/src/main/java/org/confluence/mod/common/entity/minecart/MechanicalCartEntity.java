package org.confluence.mod.common.entity.minecart;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MechanicalCartEntity extends BaseMinecartEntity {
    public MechanicalCartEntity(EntityType<? extends BaseMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MechanicalCartEntity(Level level, double x, double y, double z, Abilities<? extends MechanicalCartEntity> abilities) {
        super(level, x, y, z, abilities);
    }

    // todo
}
