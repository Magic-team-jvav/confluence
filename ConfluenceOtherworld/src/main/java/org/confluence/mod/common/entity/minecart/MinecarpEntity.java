package org.confluence.mod.common.entity.minecart;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 鲤鱼矿车。
 *
 * <p>当前阶段先继承基础矿车的速度、掉落物、持久化与撞击伤害逻辑。
 * 如果后续要还原泰拉中的专属水面或跳跃表现，应在本类内补充，避免把变体判断塞回基础矿车。</p>
 */
public class MinecarpEntity extends BaseMinecartEntity {
    public MinecarpEntity(EntityType<? extends BaseMinecartEntity> entityType, Level level) {
        super(entityType, level);
    }

    public MinecarpEntity(Level level, double x, double y, double z, Abilities<? extends BaseMinecartEntity> abilities) {
        super(level, x, y, z, abilities);
    }

}
