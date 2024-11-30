package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.AnvilBlock;


public class LeadAnvilBlock extends AnvilBlock {
    public static final MapCodec<LeadAnvilBlock> CODEC = simpleCodec(LeadAnvilBlock::new);

    public LeadAnvilBlock(Properties properties) {
        super(properties);
    }
}
