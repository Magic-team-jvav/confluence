package org.confluence.mod.common.entity.projectile.boulder;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// TODO 弹力
public class BouncyBoulderEntity extends BoulderEntity {
    public BouncyBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BouncyBoulderEntity(EntityType<? extends BoulderEntity> entityType, Level level, Vec3 pos, BlockState blockState) {
        super(entityType, level, pos, blockState);
    }

    public BouncyBoulderEntity(Level level, Vec3 pos, BlockState blockState) {
        super(level, pos, blockState);
    }
}
