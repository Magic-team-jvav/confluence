package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class GrassSeedItem extends Item {
    private final Map<Block, Block> blockMapping;

    public GrassSeedItem(Map<Block, Block> blockMapping) {
        super(new Properties().stacksTo(64));
        this.blockMapping = blockMapping;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = level.getBlockState(pos);
        if (!level.isClientSide) {
            Block currentBlock = state.getBlock();
            Block newBlock = blockMapping.get(currentBlock);
            if (newBlock != null) {
                level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}

