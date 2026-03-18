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

public class LunartearOreBlock extends Block {
    public LunartearOreBlock() {
        super(Properties.of()
                .randomTicks()
                .mapColor(MapColor.COLOR_CYAN)
                .requiresCorrectToolForDrops()
                .strength(30.0F, ModBlocks.getObsidianBasedExplosionResistance(100))
                .sound(SoundType.STONE));
    }

}
