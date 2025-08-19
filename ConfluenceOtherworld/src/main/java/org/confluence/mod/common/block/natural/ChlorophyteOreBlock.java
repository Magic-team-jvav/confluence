package org.confluence.mod.common.block.natural;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.phase_journey.common.util.PhaseUtils;

public class ChlorophyteOreBlock extends Block {
    public static final ResourceLocation PHASE = Confluence.asResource("chlorophyte");

    public ChlorophyteOreBlock() {
        super(Properties.of()
                .randomTicks()
                .mapColor(MapColor.COLOR_GREEN)
                .requiresCorrectToolForDrops()
                .strength(30.0F, 1200.0F)
                .sound(SoundType.ANCIENT_DEBRIS));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (PhaseUtils.hadLevelFinishedPhase(PHASE, level)) {
            for (int i = 0; i < 4; i++) {
                Direction direction = Util.getRandom(LibUtils.DIRECTIONS, random);
                BlockPos relative = pos.relative(direction);
                if (level.isLoaded(relative) && level.getBlockState(relative).is(Blocks.MUD)) {
                    if (level.setBlockAndUpdate(relative, defaultBlockState())) break;
                }
            }
        }
    }
}
