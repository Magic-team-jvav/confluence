package org.confluence.mod.common.block.natural;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.OverworldUtils;

public class LifeFruitBlock extends Block {
    public static final int CLASSIC_CHANCE = 1000;

    public LifeFruitBlock() {
        super(Properties.ofFullCopy(Blocks.SHORT_GRASS));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Projectile) {
            level.destroyBlock(pos, true, LibEntityUtils.getOwner(entity));
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(NatureBlocks.JUNGLE_GRASS_BLOCK);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) level.scheduleTick(pos, this, 1);
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    /// [生命果](https://terraria.wiki.gg/zh/wiki/%E7%94%9F%E5%91%BD%E6%9E%9C)
    public static boolean canGrow(ServerLevel level, BlockState state, BlockPos pos, RandomSource random) {
        boolean isExpert = LibUtils.isAtLeastExpert(level, pos);
        if (state.canBeReplaced() &&
                pos.getY() < OverworldUtils.getSurfaceY() &&
                random.nextInt(isExpert ? CLASSIC_CHANCE : CLASSIC_CHANCE * 5 / 4) == 0 &&
                KillBoard.INSTANCE.isAnyMechBossDefeated()
        ) {
            int range = (isExpert ? 122 : 102) / 3;
            Long2ObjectMap<ChunkAccess> map = new Long2ObjectArrayMap<>();
            for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range))) {
                ChunkAccess access = map.computeIfAbsent(ChunkPos.asLong(blockPos), l -> LibUtils.getChunkIfLoaded(level, ChunkPos.getX(l), ChunkPos.getZ(l)));
                if (access != null && access.getBlockState(blockPos).is(NatureBlocks.LIFE_FRUIT)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
