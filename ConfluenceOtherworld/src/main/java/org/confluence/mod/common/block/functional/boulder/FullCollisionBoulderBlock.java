package org.confluence.mod.common.block.functional.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;

public class FullCollisionBoulderBlock extends BoulderBlock {

    // 完整 16x16x16 标准方块碰撞箱
    private static final VoxelShape FULL_BLOCK_SHAPE = Shapes.box(0, 0, 0, 1, 1, 1);

    public FullCollisionBoulderBlock() {
        this(BoulderEntity::new);
    }

    public FullCollisionBoulderBlock(BoulderFactory factory) {
        this(Properties.of(), factory);
    }

    public FullCollisionBoulderBlock(Properties properties, BoulderFactory factory) {
        super(properties, factory);
    }

    // 覆盖碰撞箱 → 使用完整方块
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return FULL_BLOCK_SHAPE;
    }
}
