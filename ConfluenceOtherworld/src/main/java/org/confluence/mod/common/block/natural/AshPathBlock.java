package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.confluence.mod.common.init.block.NatureBlocks;

public class AshPathBlock extends DirtPathBlock {
    public AshPathBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState();
        BlockPos pos = context.getClickedPos();
        return state.canSurvive(context.getLevel(), pos)
                ? super.getStateForPlacement(context)
                : Block.pushEntitiesUp(state, NatureBlocks.ASH_BLOCK.get().defaultBlockState(), context.getLevel(), pos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState blockstate = pushEntitiesUp(state, NatureBlocks.ASH_BLOCK.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, blockstate);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, blockstate));
    }
}
