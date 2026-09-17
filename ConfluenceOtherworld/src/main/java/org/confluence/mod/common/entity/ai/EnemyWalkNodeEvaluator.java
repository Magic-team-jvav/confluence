package org.confluence.mod.common.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class EnemyWalkNodeEvaluator extends WalkNodeEvaluator {
    public EnemyWalkNodeEvaluator() {
        setCanPassDoors(true);
    }

    @Override
    protected BlockPathTypes evaluateBlockPathType(BlockGetter level, BlockPos origin, BlockPathTypes type) {
        BlockPathTypes result = super.evaluateBlockPathType(level, origin, type);
        // 只取消从轨道外进入轨道的限制，保留门、危险地形和实际碰撞的判定。
        return type == BlockPathTypes.RAIL && result == BlockPathTypes.UNPASSABLE_RAIL ? BlockPathTypes.RAIL : result;
    }
}
