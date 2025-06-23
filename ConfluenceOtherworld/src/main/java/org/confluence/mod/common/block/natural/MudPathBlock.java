package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import org.confluence.mod.common.block.natural.spreadable.SpreadingGrassBlock;
import org.confluence.mod.common.init.block.NatureBlocks;

import javax.annotation.Nullable;
import java.util.UUID;

public class MudPathBlock extends DirtPathBlock {
    public MudPathBlock(Properties properties) {
        super(properties);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_153131_) {
        return !this.defaultBlockState().canSurvive(p_153131_.getLevel(), p_153131_.getClickedPos())
                ? Block.pushEntitiesUp(this.defaultBlockState(), Blocks.MUD.defaultBlockState(),
                p_153131_.getLevel(), p_153131_.getClickedPos())
                : super.getStateForPlacement(p_153131_);
    }
    public static void turnToMud(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState blockstate = pushEntitiesUp(state, Blocks.MUD.defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, blockstate);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
    }
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        turnToMud(null, state, level, pos);
    }
}
