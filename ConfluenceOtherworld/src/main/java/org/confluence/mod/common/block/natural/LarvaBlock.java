package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.HorizontalDirectionalWithVerticalTwoPartBlock;
import org.confluence.terraentity.entity.boss.QueenBee;
import org.confluence.terraentity.init.TEEntities;

public class LarvaBlock extends HorizontalDirectionalWithVerticalTwoPartBlock {
    public static final MapCodec<LarvaBlock> CODEC = simpleCodec(LarvaBlock::new);

    public LarvaBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<LarvaBlock> codec() {
        return CODEC;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(PART).isBase()) {
            BlockPos below = pos.below();
            return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
        } else {
            BlockPos above = pos.above();
            return level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        level.scheduleTick(pos, this, 1);
        return state;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(state, level, pos, pNewState, pMovedByPiston);
        if (state.getValue(PART).isBase()) {
            Vec3 center = pos.getCenter();
            QueenBee queenBee = new QueenBee(TEEntities.QUEEN_BEE.get(), level);
            queenBee.setPos(center.x + level.random.nextInt(-50, 51), center.y, center.z + level.random.nextInt(-50, 51));
            level.addFreshEntity(queenBee);
            Player nearestPlayer = level.getNearestPlayer(queenBee, 200);
            if (nearestPlayer != null) queenBee.setTarget(nearestPlayer);
        }
    }
}
