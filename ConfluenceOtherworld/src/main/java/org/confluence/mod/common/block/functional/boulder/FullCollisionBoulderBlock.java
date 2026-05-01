package org.confluence.mod.common.block.functional.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.entity.projectile.boulder.BoulderEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

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
