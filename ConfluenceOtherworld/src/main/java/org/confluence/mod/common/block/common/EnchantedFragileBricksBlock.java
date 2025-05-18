package org.confluence.mod.common.block.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.functional.FragileBlock;
import org.confluence.mod.common.init.item.HammerItems;

import java.util.function.Supplier;

public class EnchantedFragileBricksBlock extends FragileBlock {
    public EnchantedFragileBricksBlock(Properties pProperties, Supplier<BlockState> simulatorBlock) {
        super(pProperties, simulatorBlock);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        return player.getMainHandItem().is(HammerItems.PWNHAMMER) ? super.getDestroyProgress(state, player, level, pos) : 0;
    }
}
