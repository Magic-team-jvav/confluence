package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.MapColor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.GlobalCloakData;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.mixed.ILevelChunkSection;

public class ChlorophyteOreBlock extends Block {
    public ChlorophyteOreBlock() {
        super(Properties.of()
                .randomTicks()
                .mapColor(MapColor.COLOR_GREEN)
                .requiresCorrectToolForDrops()
                .strength(30.0F, ModBlocks.getObsidianBasedExplosionResistance(100))
                .sound(SoundType.ANCIENT_DEBRIS));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!GlobalCloakData.INSTANCE.isRevealed(state)) return;
        if (level.random.nextInt(10000) != 0) return;
        for (int i = 0; i < 4; i++) {
            Direction direction = Direction.getRandom(random);
            BlockPos relative = pos.relative(direction);
            BlockState relState = level.getBlockState(relative);
            ChunkAccess chunk;
            if (level.isOutsideBuildHeight(pos) || ((chunk = LibUtils.getChunkIfLoaded(level, relative)) == null)) {
                continue;
            }
            ILevelChunkSection section = ILevelChunkSection.of(chunk.getSection(level.getSectionIndex(relative.getY())));
            if (section.confluence$getBlockCounts().chlorophyte.get() > 125) {
                continue;
            }
            if (level.isLoaded(relative) && (relState.is(Blocks.MUD) || (relState.is(NatureBlocks.JUNGLE_GRASS_BLOCK) && !level.canSeeSky(relative)))) {
                if (level.setBlockAndUpdate(relative, defaultBlockState())) break;
            }
        }
    }
}
