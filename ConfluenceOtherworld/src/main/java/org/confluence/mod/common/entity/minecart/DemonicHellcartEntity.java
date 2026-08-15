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

    /*
     * 恶魔地狱矿车当前复用基础矿车能力。
     * 火焰尾迹、火免疫以外的专属效果后续放在本类实现，避免和其他矿车变体耦合。
     */
}
