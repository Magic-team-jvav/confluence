package org.confluence.mod.common.worldgen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.function.Function;

public class JungleCaveCarver extends CaveWorldCarver {
    public JungleCaveCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean carveBlock(
            CarvingContext context,
            CaveCarverConfiguration config,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeGetter,
            CarvingMask carvingMask,
            BlockPos.MutableBlockPos pos,
            BlockPos.MutableBlockPos checkPos,
            Aquifer aquifer,
            MutableBoolean reachedSurface
    ) {
        BlockState blockstate = chunk.getBlockState(pos);
        if (reachedSurface.isFalse() && (blockstate.is(Blocks.MUD) || blockstate.is(NatureBlocks.JUNGLE_GRASS_BLOCK.get()))) {
            reachedSurface.setTrue();
        }

        if (!canReplaceBlock(config, blockstate)) {
            return false;
        }
        BlockState blockstate1 = getCarveState(context, config, pos, aquifer);
        if (blockstate1 == null) {
            return false;
        }
        chunk.setBlockState(pos, blockstate1, false);
        if (aquifer.shouldScheduleFluidUpdate() && !blockstate1.getFluidState().isEmpty()) {
            chunk.markPosForPostprocessing(pos);
        }

        if (reachedSurface.isTrue()) {
            checkPos.setWithOffset(pos, Direction.DOWN);
            if (chunk.getBlockState(checkPos).is(Blocks.MUD)) {
                context.topMaterial(biomeGetter, chunk, checkPos, !blockstate1.getFluidState().isEmpty()).ifPresent(blockState -> {
                    chunk.setBlockState(checkPos, blockState, false);
                    if (!blockState.getFluidState().isEmpty()) {
                        chunk.markPosForPostprocessing(checkPos);
                    }
                });
            }
        }

        return true;
    }
}
