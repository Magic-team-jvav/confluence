package org.confluence.mod.common.item.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.tags.TagKey;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class GrassSeedItem extends Item {
    private final Map<Block, Block> blockMapping;
    private final Map<TagKey<Block>, Block> tagMapping;

    public GrassSeedItem(Map<Block, Block> blockMapping) {
        super(new Properties());
        this.blockMapping = blockMapping;
        this.tagMapping = new HashMap<>();
    }

    public GrassSeedItem(Map<Block, Block> blockMapping, Map<TagKey<Block>, Block> tagMapping) {
        super(new Properties());
        this.blockMapping = new HashMap<>(blockMapping);
        this.tagMapping = new HashMap<>(tagMapping);
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
                ItemStack stack = pContext.getItemInHand();
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
            if (tagMapping != null) {
                for (Map.Entry<TagKey<Block>, Block> entry : tagMapping.entrySet()) {
                    if (state.is(entry.getKey())) {
                        Block tagMappedBlock = entry.getValue();
                        if (tagMappedBlock != null) {
                            level.setBlockAndUpdate(pos, tagMappedBlock.defaultBlockState());
                            ItemStack stack = pContext.getItemInHand();
                            stack.shrink(1);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}


