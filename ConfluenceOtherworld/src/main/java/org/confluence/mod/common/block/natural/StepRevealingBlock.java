package org.confluence.mod.common.block.natural;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.util.Lazy;
import org.confluence.mod.common.init.block.OreBlocks;

public class StepRevealingBlock extends Block {
    public static final IntegerProperty REVEAL_STEP = IntegerProperty.create("reveal_step", 0, 2);
    public static final Lazy<BlockState[][]> PAIRS = Lazy.of(() -> {
        BlockState[][] pairs = new BlockState[9][2];
        for (int i = 0; i < 3; i++) {
            int j = i * 3;
            pairs[j] = new BlockState[]{
                    OreBlocks.DEEPSLATE_COBALT_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, i),
                    OreBlocks.DEEPSLATE_PALLADIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, i)
            };
            pairs[j + 1] = new BlockState[]{
                    OreBlocks.DEEPSLATE_MYTHRIL_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, i),
                    OreBlocks.DEEPSLATE_ORICHALCUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, i)
            };
            pairs[j + 2] = new BlockState[]{
                    OreBlocks.DEEPSLATE_ADAMANTITE_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, i),
                    OreBlocks.DEEPSLATE_TITANIUM_ORE.get().defaultBlockState().setValue(StepRevealingBlock.REVEAL_STEP, i)
            };
        }
        return pairs;
    });

    public StepRevealingBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(REVEAL_STEP, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REVEAL_STEP);
    }
}
