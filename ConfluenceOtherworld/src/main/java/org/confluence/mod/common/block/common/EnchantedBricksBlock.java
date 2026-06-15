package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.init.item.HammerItems;

public class EnchantedBricksBlock extends Block {
    public EnchantedBricksBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return player.getMainHandItem().is(HammerItems.PWNHAMMER.get()) ? super.getDestroyProgress(state, player, level, pos) : 0;
    }
}
