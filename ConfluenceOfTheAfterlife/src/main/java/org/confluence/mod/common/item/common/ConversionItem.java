package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class ConversionItem extends Item {
    private final Map<Block, Block> blockMap;
    private final int size;

    public ConversionItem(Properties properties, Map<Block, Block> blockMap) {
        this(properties, blockMap, 1);
    }

    public ConversionItem(Properties properties, Map<Block, Block> blockMap, int size) {
        super(properties);
        this.blockMap = blockMap;
        this.size = size;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Block clickedBlock = level.getBlockState(pos).getBlock();
        if (blockMap.containsKey(clickedBlock) && !level.isClientSide) {
            if (size == 1){
                level.setBlock(pos, blockMap.get(clickedBlock).defaultBlockState(), 3);
            } else {
                for (int x = -size + 1; x < size; x++){
                    for (int y = -size + 1; y < size; y++){
                        for (int z = -size + 1; z < size; z++){
                            BlockPos pos2 = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                            Block block2 = level.getBlockState(pos2).getBlock();
                            if (blockMap.containsKey(block2)){
                                level.setBlock(pos2, blockMap.get(block2).defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    public int getSize() {
        return size;
    }

    public Map<Block, Block> getBlockMap() {
        return blockMap;
    }
}