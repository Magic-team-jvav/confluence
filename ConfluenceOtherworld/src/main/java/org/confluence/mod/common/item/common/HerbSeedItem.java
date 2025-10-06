package org.confluence.mod.common.item.common;

import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;

// ItemModel datagen指示器
public class HerbSeedItem extends ItemNameBlockItem {
    public HerbSeedItem(Block pBlock){
        super(pBlock, new Properties());
    }

    public HerbSeedItem(Block pBlock, Properties properties){
        super(pBlock, properties);
    }
}
