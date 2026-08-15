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

    /*
     * 机械矿车当前复用基础高速矿车能力。
     * 若要补充激光、粒子或额外撞击效果，应在本类覆盖 tick 追加，保持基础类只承载公共规则。
     */
}
