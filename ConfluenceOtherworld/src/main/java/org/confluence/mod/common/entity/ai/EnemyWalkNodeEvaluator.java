package org.confluence.mod.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class EnemyWalkNodeEvaluator extends WalkNodeEvaluator {
    public EnemyWalkNodeEvaluator() {
        setCanPassDoors(true);
    }

    @Override
    public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
        PathType result = super.getPathTypeOfMob(context, x, y, z, mob);
        if (result != PathType.UNPASSABLE_RAIL) return result;
        BlockState state = context.getBlockState(new BlockPos(x, y, z));
        // 只取消「从轨道外进入轨道」的限制，保留门、危险地形和实际碰撞的判定。
        return state.is(BlockTags.RAILS) ? PathType.RAIL : result;
    }
}
