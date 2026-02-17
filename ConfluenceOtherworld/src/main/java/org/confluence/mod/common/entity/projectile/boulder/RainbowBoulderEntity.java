package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEntities;

// TODO 彩虹
public class RainbowBoulderEntity extends BoulderEntity {
    public RainbowBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public RainbowBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(ModEntities.RAINBOW_BOULDER.get(), level, pos, blockState);
    }
}
