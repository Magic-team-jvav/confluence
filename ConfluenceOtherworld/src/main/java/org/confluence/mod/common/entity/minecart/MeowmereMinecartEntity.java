package org.confluence.mod.common.entity.minecart;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MeowmereMinecartEntity extends BaseMinecartEntity {
    public MeowmereMinecartEntity(EntityType<? extends BaseMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MeowmereMinecartEntity(Level level, double x, double y, double z, Abilities<? extends BaseMinecartEntity> abilities) {
        super(level, x, y, z, abilities);
    }

    /*
     * 喵星矿车的彩虹尾迹和喵刃弹射属于变体特效。
     * 基础矿车只负责通用乘坐、速度和掉落；专属表现后续应集中写在本类，避免影响普通矿车。
     */
}
